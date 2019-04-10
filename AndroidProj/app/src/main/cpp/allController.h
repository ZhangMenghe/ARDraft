#ifndef ARCALVR_ALL_CONTROLLER_H
#define ARCALVR_ALL_CONTROLLER_H

#include <android/asset_manager.h>

class allController{
private:
    AAssetManager * _asset_manager;
    cv::Mat _frame;
public:
    allController(AAssetManager *assetManager);
    void onDrawFrame(cv::Mat * mat);
};
#endif