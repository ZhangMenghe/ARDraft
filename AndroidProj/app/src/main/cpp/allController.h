#ifndef ARCALVR_ALL_CONTROLLER_H
#define ARCALVR_ALL_CONTROLLER_H

#include <android/asset_manager.h>
#include "System.h"
class allController{
private:
    AAssetManager * _asset_manager;
    cv::Mat _frame;
    std::vector<cv::Mat> voc_images;
    int frame_count_ = 0;

    void constructVOC();
    void loadFeatures(std::vector<std::vector<cv::Mat > > &features);
    void changeStructure(const cv::Mat &plain, std::vector<cv::Mat> &out);
    void testVocCreation(const std::vector<std::vector<cv::Mat > > &features);
    void testDatabase(const std::vector<std::vector<cv::Mat > > &features);

protected:
    std::chrono::steady_clock::time_point t0;
    double ttrack=0;
    ORB_SLAM2::System* SLAM = nullptr;
public:
    allController(AAssetManager *assetManager);
    void onDrawFrame(cv::Mat * mat);
};
#endif