import os
import cv2
import csv
import json
import traceback
import torch
import dlib
#import face_recognition

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

def objectDetect(jsonObject):
    imageJson = json.loads(jsonObject)

    common_path = os.path.dirname(os.path.abspath(__file__))

    try:
        articleId = int(imageJson["articleId"]) # 서버에서 실행 시
        print("매개변수 확인 성공: articleId={}".format(articleId))
    except:
        articleId = 16 # 테스트용 코드(직접 실행 시)
        print("매개변수 확인 불가, 테스트 값 적용: articleId={}".format(articleId))

    try:
        print(">> YOLOv5 모델 로딩") # YOLOv5 모델 불러오기
        # human_weights_path = common_path + "/yolov5/custom/human.pt"
        # food_weights_path = common_path + "/yolov5/custom/food.pt"
        # human_model = torch.hub.load("ultralytics/yolov5", "custom", path=human_weights_path, force_reload=True)
        # food_model = torch.hub.load("ultralytics/yolov5", "custom", path=food_weights_path, force_reload=True)

        print(">> dlib face_detection 모델 로딩")
        cnn_face_detector = dlib.cnn_face_detection_model_v1(common_path + '/config/model/mmod_human_face_detector.dat')

        print(">> 클래스 리스트(CSV) 로딩") # 클래스 리스트 읽기
        class_list = []
        class_list = loadCSVRow(common_path + '/config/ClassList.csv')

        # 작업할 이미지 목록 생성
        print(">> 전체 json에서 필요 데이터 추출(dict)")
        image_dict = {}
        for image in imageJson["Item"]:
            item_dict = {}

            item_dict["imageName"] = image["imageName"]
            item_dict["imageAddress"] = image["imageAddress"]

            imageId = int(image["imageId"])
            image_dict[imageId] = item_dict

        #print(json.dumps(image_dict, indent=4)) # 테스트용 코드
        
        # CSV 파일 생성과 데이터 저장
        print(">> 결과 저장할 CSV 파일 생성")
        csv_filepath = common_path + "/result/Image_{}.csv".format(articleId)  # 저장할 CSV 파일명
        with open(csv_filepath, mode="w", newline="") as csv_file:
            fieldnames = ["imageId"]
            fieldnames.extend(class_list) # 불러온 클래스 리스트 추가

            print(">> CSV 열 이름 설정")
            print("fieldNames: {}".format(fieldnames))
            writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
            writer.writeheader()

            print(">> CSV 행 데이터 작성")
            for imageId in image_dict:
                image_path = image_dict[imageId]["imageAddress"]

                # 이미지 불러오기
                scale = 0.5
                img = cv2.imread(image_path)  # 이미지 로드
                img_resized = cv2.resize(img, dsize=(0,0), fx=scale, fy=scale)
                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)  # BGR을 RGB로 변환

                # YOLOv5를 통한 객체 인식
                #human_results = human_model(img)
                #food_results = food_model(img)

                # dlib 사람 인식
                face_detections = cnn_face_detector(img_resized, 1)
                human_count = -1
                for idx, face_detection in enumerate(face_detections):
                    human_count = idx
                human_count = human_count + 1

                # 인식된 객체 수 카운트
                #human_count = (human_results.pred[0][:, -1] == 0).sum().item()
                #food_count = (food_results.pred[0][:, -1] == 0).sum().item()

                # 데이터 저장
                #writer.writerow({"imageId": imageId, "human": human_count, "food": food_count})
                writer.writerow({"imageId": imageId, "human": human_count})

        print(f"csv_filePath: {csv_filepath}")
    
    except FileNotFoundError:
        print_except("파일을 찾을 수 없음")
    except KeyError:
        print_except("잘못된 딕셔너리 키 사용")
    except IndexError:
        print_except("인덱스 범위를 벗어남")
    except:
        print_except("알 수 없는 오류 발생")

if __name__ == '__main__':
    objectDetect()






"""
# YOLOv5 모델 불러오기
    human_weights_path = "./yolov5/custom/human.pt"
    food_weights_path = "./yolov5/custom/food.pt"
    human_model = torch.hub.load("ultralytics/yolov5", "custom", path=human_weights_path, force_reload=True)
    food_model = torch.hub.load("ultralytics/yolov5", "custom", path=food_weights_path, force_reload=True)

    # 이미지 폴더 경로
    image_folder = "./yolov5/image/*.jpg"

    # 이미지 파일 리스트 얻어오기
    image_files = glob.glob(image_folder)
"""