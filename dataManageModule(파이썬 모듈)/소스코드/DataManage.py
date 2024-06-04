#-*- coding:utf-8 -*-
import sys
import csv
import json
import requests
import os
import traceback
from difflib import SequenceMatcher

from Image_object_detection import objectDetect
from Section_word_extraction import wordExtract

#class_list = ['human', 'food'] #추후 csv 파일 불러와서 저장
class_list = ['human'] #추후 csv 파일 불러와서 저장

# target = sectionId/imageId
def read_csv_parse(path, target):
    return_dict = {}
    
    with open(path, mode='r', newline='', encoding='UTF8') as f:
        reader = csv.DictReader(f)

        # sectionId/imageId 기준으로 데이터 구조화
        for row_dict in reader:
            key = int(row_dict[target])
            
            data_dict, raw_dict = {}, {} # 임시 저장 딕셔너리 생성
            data_dict[key], raw_dict[key] = {}, {} # section별 딕셔너리 생성(2중 딕셔너리)
            result_keyword = [] # section별 키워드 저장할 리스트

            # 필요 클래스 추출(필요 클래스 수 만큼만 반복)
            for class_string in class_list:
                raw_dict[key][class_string] = int(row_dict[class_string])

                # 필요 클래스의 count 수만큼 리스트에 추가
                for i in range(int(row_dict[class_string])):
                    result_keyword.append(class_string)
            
            data_dict[key] = result_keyword
            return_dict[key] = result_keyword

            # print(raw_dict)
            # print(return_dict)

    return return_dict

def similarity(section_dict, image_dict):
    sec_dict = section_dict
    img_dict = image_dict

    result = {}

    for section in sec_dict:
        max = -1.0
        max_image = -1

        for image in img_dict:
            ratio = SequenceMatcher(None, sec_dict[section], img_dict[image]).ratio()
            if max < ratio:
                max = ratio
                max_image = image

        result[section] = max_image
    
    return result

def print_except(msg):
    print(msg)
    print(traceback.format_exc())

def main():
    print("==python==")

    try:
        articleId = int(sys.argv[1]) # 서버에서 실행 시
        print("매개변수 확인 성공: articleId={}".format(articleId))
    except:
        articleId = 1 # 테스트용 코드(직접 실행 시)
        print("매개변수 확인 불가, 테스트 값 적용: articleId={}".format(articleId))

    try:
        print(">> image/section 데이터 요청")
        headers = {'Content-Type': 'application/json; charset=utf-8'}
        getImage_url = "http://127.0.0.1:8080/get/image"
        getSection_url = "http://127.0.0.1:8080/get/section"

        print(">> 요청 json 생성(articleId)")
        get_param = {}
        get_param["articleId"] = "{}".format(articleId)
        get_JsonString = json.dumps(get_param)
        #print(get_JsonString)

        print(">> 서버 연결 및 요청")
        getImage_response = requests.post(getImage_url, headers=headers, data=get_JsonString)
        getSection_response = requests.post(getSection_url, headers=headers, data=get_JsonString)

        print(">> 반환값 jsonObject 변환")
        imageJsonObj = json.dumps(getImage_response.json(), indent=4, ensure_ascii=False)
        sectionJsonObj = json.dumps(getSection_response.json(), indent=4, ensure_ascii=False)
        print("[imageJson]\n" + imageJsonObj + "\n")
        print("[sectionJson]\n" + sectionJsonObj)


        ########## Image_object_detection.py ##########
        print("========== Image Object Detection ==========")
        objectDetect(imageJsonObj)
        print("========== Image Object Detection(Finish) ==========\n")
        ########## Image_object_detection.py ##########


        ########## Section_word_extraction.py ##########
        print("========== Section Word Extraction ==========")
        wordExtract(sectionJsonObj)
        print("========== Section Word Extraction(Finish) ==========\n")
        ########## Section_word_extraction.py ##########


        # csv 파일 경로 설정
        print(">> CSV 파일 경로 설정")
        common_path = os.path.dirname(os.path.abspath(__file__))
        section_csv_path = common_path + r"\result\Section_{}.csv".format(articleId)
        image_csv_path = common_path + r"\result\Image_{}.csv".format(articleId)

        print(section_csv_path)
        print(image_csv_path)

        # csv 데이터 > 파이썬 리스트
        print(">> CSV 데이터 > 파이썬 리스트 변환")
        section_dict = read_csv_parse(section_csv_path, 'sectionId')
        image_dict = read_csv_parse(image_csv_path, 'imageId')

        print(">> 이미지/텍스트 유사도 비교 및 매칭")
        items = similarity(section_dict, image_dict)
        print(items)
        countItem = len(items)

        print(">> 딕셔너리 데이터 생성")
        tmp_dict = {}
        tmp_dict["articleId"] = articleId
        tmp_dict["countItem"] = countItem
        tmp_dict["items"] = list()
        for sectionIdTmp in items:
            tmp_dict["items"].append({
                "sectionId": sectionIdTmp,
                "imageId": items[sectionIdTmp]
            })

        print(">> 딕셔너리 데이터 > Json 데이터로 변환")
        jsonString = json.dumps(tmp_dict, indent=4)

        print(">> 서버 연결")
        print(jsonString)
        headers = {'Content-Type': 'application/json; charset=utf-8'}
        url = "http://127.0.0.1:8080/upload/package"

        response = requests.post(url, headers=headers, data=jsonString)

        if (response.json()["status"] == 500):
            print("response 비정상 > status: 500")
            print(response.json())
        elif (response.json()["msg"] == "OK"):
            print("response 정상")

    except FileNotFoundError:
        print_except("파일을 찾을 수 없음")
    except KeyError:
        print_except("잘못된 딕셔너리 키 사용")
    except IndexError:
        print_except("인덱스 범위를 벗어남")
    except requests.exceptions.ConnectionError:
        print_except("서버와 연결하지 못함")
    except:
        print_except("알 수 없는 오류 발생")

    # for section in section_dict:
    #     print(section)

    # print(section_dict)
    # print(image_dict)

if __name__ == '__main__':
    main()











# def read_section_csv(path):
#     return_dict = {}

#     with open(path, mode='r', newline='') as f:
#         reader = csv.DictReader(f)

#         # sectionId 기준으로 데이터 구조화
#         for row_dict in reader:
#             sectionId = int(row_dict['sectionId'])

#             data_dict, raw_dict = {}, {} # 임시 저장 딕셔너리 생성
#             data_dict[sectionId], raw_dict[sectionId] = {}, {} # section별 딕셔너리 생성(2중 딕셔너리)
#             section_keyword = [] # section별 키워드 저장할 리스트

#             # 필요 클래스 추출(필요 클래스 수 만큼만 반복)
#             for class_string in class_list:
#                 raw_dict[sectionId][class_string] = int(row_dict[class_string])

#                 # 필요 클래스의 count 수만큼 리스트에 추가
#                 for i in range(int(row_dict[class_string])):
#                     section_keyword.append(class_string)
            
#             data_dict[sectionId] = section_keyword
#             return_dict[sectionId] = section_keyword

#             print(raw_dict)
#             print(return_dict)

#     return return_dict








# if (section_keyword.count != image_keyword.count):
#     print("section 개수와 image 개수가 일치하지 않음 > article: {}, section: {}".format(article_keyword.count, section_keyword.count))
#     exit



# from difflib import SequenceMatcher

# str1 = ['human', 'human', 'food']
# str2 = ['human', 'human']



# ratio = SequenceMatcher(None, str1, str2).ratio()
# print(ratio)