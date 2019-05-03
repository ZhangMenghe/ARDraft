#include <android/asset_manager.h>

#include <opencv2/core.hpp>

#include "allController.h"
#include <iostream>
#include <Timestamp.h>
#include <FORB.h>
#include <Eigen/Dense>
allController::allController(AAssetManager *assetManager):_asset_manager(assetManager){
    Eigen::MatrixXd M(2,2);
    Eigen::MatrixXd V(2,2);
}

void allController::onDrawFrame(cv::Mat *mat) {
    _frame = *mat;
}