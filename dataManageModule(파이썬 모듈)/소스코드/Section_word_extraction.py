import os
import sys
import csv
import json
import traceback

from konlpy.tag import Komoran
from transformers import BertModel, BertTokenizer
import torch.nn.functional as F

def loadCSVRow(path):
    with open(path, mode='r', newline='') as f:
        csv_list = []

        reader = csv.reader(f)
        for row in reader:
            csv_list.extend(row)

        return csv_list

def print_except(msg):
    print(msg)
    print(traceback.format_exc())

def wordExtract(jsonObject):
    sectionJson = json.loads(jsonObject)
    #print(json.dumps(sectionJson, indent=4, ensure_ascii=False))

    common_path = os.path.dirname(os.path.abspath(__file__))

    try:
        articleId = int(sectionJson["Item"][0]["articleId"]) # 서버에서 실행 시
        print("매개변수 확인 성공: articleId={}".format(articleId))
    except:
        articleId = 16 # 테스트용 코드(직접 실행 시)
        print("매개변수 확인 불가, 테스트 값 적용: articleId={}".format(articleId))

    print(">> KoBERT/Tokenizer 로딩")
    model = BertModel.from_pretrained('monologg/kobert')
    tokenizer = BertTokenizer.from_pretrained('monologg/kobert')

    # 형태소 분석기 초기화
    komoran = Komoran()

    print(">> 전체 json애서 필요 데이터 추출(dict)")
    section_dict = {}
    for section in sectionJson["Item"]:
        section_dict[section["sectionId"]] = section["sectionText"]

    print(json.dumps(section_dict, indent=4, ensure_ascii=False))

    # 카테고리 및 상세 키워드 정의
    category_keywords = {
        'PS': ['사람','혼자','친구', '부모님', '엄마', '아빠', '동생', '누나', '형', '오빠', '언니', '할머니', '할아버지', '아들', '딸', '선배', '후배', '동기'],
        'FD': ['떡볶이', '음식', '밥', '아침', '점심', '저녁'],
        'LC': ['바닷가', '음식점'],
        'PT': ['오늘', '어제']
    }

    print(">> 클래스 리스트(CSV) 로딩") # 클래스 리스트 읽기
    class_list = []
    class_list = loadCSVRow(common_path + '/config/ClassList.csv')

    # 섹션별 NER 추출 및 결과 저장
    output_filename = common_path + '/result/Section_{}.csv'.format(articleId)
    with open(output_filename, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['sectionId']
        fieldnames.extend(class_list)

        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for sectionId in section_dict:
            sectionText = section_dict[sectionId]

            # Perform Korean morphological analysis
            tokens = komoran.morphs(sectionText)
            section_text_morphs = ' '.join(tokens)

            # Categorize keywords based on predefined keywords
            categorized_keywords = {}
            skip_next = False
            for idx, token in enumerate(tokens):
                if skip_next:
                    skip_next = False
                    continue

                if token in ['오늘', '어제'] and idx < len(tokens) - 1 and tokens[idx + 1] == '바닷가':
                    categorized_keywords.setdefault('LC', []).append(token)
                elif token == '만나' and idx < len(tokens) - 1 and tokens[idx + 1] == token:
                    skip_next = True
                
                else:
                    for category, keywords in category_keywords.items():
                        if any(keyword in token for keyword in keywords):
                            categorized_keywords.setdefault(category, []).append(token)

            # Remove duplicates from categorized keywords
            for category in categorized_keywords:
                categorized_keywords[category] = list(set(categorized_keywords[category]))

            # Create detailed_categories string
            detailed_categories = ', '.join([f'{category}: {", ".join(tokens)}' for category, tokens in categorized_keywords.items()])

            if 'PS' in categorized_keywords:
                humanCount = len(categorized_keywords['PS'])
            else:
                humanCount = 0
            
            if 'FD' in categorized_keywords:
                foodCount = len(categorized_keywords['FD'])
            else:
                foodCount = 0

            #writer.writerow({'sectionId': sectionId, 'human': humanCount, 'food': foodCount})
            writer.writerow({'sectionId': sectionId, 'human': humanCount})

    print(categorized_keywords)
    print(f"Detailed categorized results have been saved to '{output_filename}'.")

if __name__ == '__main__':
    wordExtract()