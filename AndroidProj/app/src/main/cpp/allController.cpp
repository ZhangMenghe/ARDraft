#include <android/asset_manager.h>

#include <opencv2/core.hpp>

#include "allController.h"
#include <iostream>

allController::allController(AAssetManager *assetManager):_asset_manager(assetManager){

}

void allController::onDrawFrame(cv::Mat *mat) {
    _frame = *mat;
}