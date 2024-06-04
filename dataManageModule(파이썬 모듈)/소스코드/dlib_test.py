import dlib
import face_recognition
import numpy as np
import cv2

image = face_recognition.load_image_file('C:\\Users\\lullu\\Desktop\\image3.jpg')

face_locations = face_recognition.face_locations(image, model="cnn")
print(face_locations)
for face_location in face_locations:
    top, right, bottom, left = face_location
    
    face_image = image[top:bottom, left:right]
    pil_image = np.array(face_image)
    cv2.imshow("image", pil_image)
    cv2.waitKey()