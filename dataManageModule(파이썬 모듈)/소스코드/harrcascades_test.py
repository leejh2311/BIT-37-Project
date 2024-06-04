import dlib
import cv2
import os

common_path = os.path.dirname(os.path.abspath(__file__))

cnn_face_detector = dlib.cnn_face_detection_model_v1(common_path + '/config/model/mmod_human_face_detector.dat')
image = cv2.imread(r'C:\Users\lullu\Desktop\test_rotate.jpg')

scale = 0.5
# Reload the resized image since the bounding-box is already drawn in the resized image.
image_resized = cv2.resize(image, dsize=(0,0), fx=scale, fy=scale)
# image_resized = image
# The second parameter is also a scale related parameter.
face_detections = cnn_face_detector(image_resized, 1)

count_face = -1

# 'confidence' is the confidence indicating the reliability of a single detection.
for idx, face_detection in enumerate(face_detections):
    left, top, right, bottom, confidence = face_detection.rect.left(), face_detection.rect.top(), face_detection.rect.right(), face_detection.rect.bottom(), face_detection.confidence
    print(f'confidence{idx+1}: {confidence}')  # print confidence of the detection
    cv2.rectangle(image_resized, (left, top), (right, bottom), (0, 255, 0), 2)
    count_face = idx

print(count_face)
cv2.imshow('image', image_resized)
cv2.waitKey()