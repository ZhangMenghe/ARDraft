cmake_minimum_required(VERSION 3.4.1)
SET(CPP_PATH "src/main/cpp")
SET(KERNEL_LIBRARIES
    EGL
    GLESv2
    GLESv3)

set(OpenCV_DIR "../opencv/native/jni")
set(OpenCV_INCLUDE_DIR ${OpenCV_DIR}/include)
INCLUDE_DIRECTORIES(${OpenCV_INCLUDE_DIR})

add_library(orbAndroid
            SHARED
            ${CPP_PATH}/allController.cpp
            ${CPP_PATH}/jni_interface.cpp)

find_package(OpenCV 3.4 REQUIRED java)

target_link_libraries(orbAndroid
                      android
                      mediandk
                      ${Opencv_Native_Libraries}
                      ${KERNEL_LIBRARIES})
