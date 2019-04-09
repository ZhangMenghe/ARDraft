cmake_minimum_required(VERSION 3.4.1)
SET(SYS_PACKAGES_PATH "/home/menghe/PACKAGES")
SET(CPP_PATH "src/main/cpp")
SET(KERNEL_LIBRARIES
    EGL
    GLESv2
    GLESv3)

set(OpenCV_DIR "${SYS_PACKAGES_PATH}/OpenCV-android-sdk/sdk/native")
# staticlibs/${ANDROID_ABI}
set(OpenCV_INCLUDE_DIR ${OpenCV_DIR}/include)
INCLUDE_DIRECTORIES(${OpenCV_INCLUDE_DIR})

add_library(orbAndroid
            SHARED
            ${CPP_PATH}/main.cpp)
target_link_libraries(orbAndroid
                      android
                      mediandk

                      ${KERNEL_LIBRARIES})
