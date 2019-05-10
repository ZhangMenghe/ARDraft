cmake_minimum_required(VERSION 3.4.1)
SET(CPP_PATH "src/main/cpp")
SET(KERNEL_LIBRARIES
    EGL
    GLESv2
    GLESv3)

SET(SYSTEM_PACKAGE_PATH "/home/menghe/PACKAGES")
SET(THIRD_PARTY_DIR ${CMAKE_SOURCE_DIR}/../ThirdParty)

#opencv
set(OpenCV_DIR "${CMAKE_SOURCE_DIR}/../opencv/native/jni")
#SET(OpenCV_DIR "${THIRD_PARTY_DIR}/opencv")
set(OpenCV_INCLUDE_DIR ${OpenCV_DIR}/include)

INCLUDE_DIRECTORIES(${OpenCV_INCLUDE_DIR})

#add_library(opencv_java3 SHARED IMPORTED)
#set_target_properties(opencv_java3 PROPERTIES IMPORTED_LOCATION
#        ${OpenCV_DIR}/libopencv_java3.so)
find_package(OpenCV 3.4 REQUIRED java)


set(DBOW2_DIR "${THIRD_PARTY_DIR}/DBoW2")
set(DBOW2_INCLUDE ${DBOW2_DIR}/include/DBoW2)
add_library(DBoW2 STATIC IMPORTED)
set_target_properties(DBoW2 PROPERTIES IMPORTED_LOCATION
        ${DBOW2_DIR}/lib/libDBoW2.a)
INCLUDE_DIRECTORIES(${DBOW2_INCLUDE})


SET(EIGEN3_INCLUDE_DIR "${SYSTEM_PACKAGE_PATH}/eigen-3.7")


SET(g2o_SOURCE_DIR "${THIRD_PARTY_DIR}/g2o")
add_library(g2o STATIC IMPORTED)
set_target_properties(g2o PROPERTIES IMPORTED_LOCATION
        ${g2o_SOURCE_DIR}/lib/libg2o.a)
INCLUDE_DIRECTORIES(
        ${g2o_SOURCE_DIR}/core
        ${g2o_SOURCE_DIR}/types
        ${g2o_SOURCE_DIR}/stuff
        ${EIGEN3_INCLUDE_DIR})

INCLUDE_DIRECTORIES(src/main/cpp/ORB/include)
add_library(ORBSLAM2
            src/main/cpp/ORB/src/System.cc
            src/main/cpp/ORB/src/Tracking.cc
            src/main/cpp/ORB/src/LocalMapping.cc
            src/main/cpp/ORB/src/LoopClosing.cc
            src/main/cpp/ORB/src/ORBextractor.cc
            src/main/cpp/ORB/src/ORBmatcher.cc
            src/main/cpp/ORB/src/FrameDrawer.cc
            src/main/cpp/ORB/src/Converter.cc
            src/main/cpp/ORB/src/MapPoint.cc
            src/main/cpp/ORB/src/KeyFrame.cc
            src/main/cpp/ORB/src/Map.cc
            src/main/cpp/ORB/src/Optimizer.cc
            src/main/cpp/ORB/src/PnPsolver.cc
            src/main/cpp/ORB/src/Frame.cc
            src/main/cpp/ORB/src/KeyFrameDatabase.cc
            src/main/cpp/ORB/src/Sim3Solver.cc
            src/main/cpp/ORB/src/Initializer.cc)

add_library(orbAndroid
        SHARED
        ${CPP_PATH}/allController.cpp
        ${CPP_PATH}/jni_interface.cpp
        ${CPP_PATH}/AndroidHelper.cpp)
#message(FATAL_ERROR "======${Opencv_Native_Libraries}")
target_link_libraries(orbAndroid
                      android
                      mediandk
                      ${ORBSLAM2}
                      DBoW2
                      g2o
                      ${OpenCV_LIBRARIES}
                      ${KERNEL_LIBRARIES})
