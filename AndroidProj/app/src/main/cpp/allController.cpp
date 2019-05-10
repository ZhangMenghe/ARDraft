#include <android/asset_manager.h>

#include <opencv2/core.hpp>

#include "allController.h"
#include "AndroidHelper.h"
#include "jni_interface.h"
#include <iostream>
#include <DBoW2.h>
#include <Eigen/Dense>
#include <opencv2/features2d.hpp>

#include "jni_interface.h"
using namespace DBoW2;
allController::allController(AAssetManager *assetManager):_asset_manager(assetManager){
//    Eigen::MatrixXd M(2,2);
//    Eigen::MatrixXd V(2,2);

}

void allController::onDrawFrame(cv::Mat *mat) {
    if(voc_images.size() > 4) return;//{voc_images.clear(); frame_count_ = 0;}
    frame_count_++;
    if(frame_count_ %50== 0){
        LOGE("===add an image=== %d", voc_images.size());
        voc_images.push_back(*mat);
        if(voc_images.size() > 3)
            constructVOC();
    }

//    _frame = *mat;
}
void allController::loadFeatures(std::vector<std::vector<cv::Mat > > &features){

    features.clear();

    cv::Ptr<cv::ORB> orb = cv::ORB::create();

    for(auto image:voc_images)
    {
        cv::Mat mask;
        std::vector<cv::KeyPoint> keypoints;
        cv::Mat descriptors;

        orb->detectAndCompute(image, mask, keypoints, descriptors);

        features.emplace_back(std::vector<cv::Mat >());
        changeStructure(descriptors, features.back());
        LOGE("===feature nums: %d", features.back().size());
    }
}
void allController::changeStructure(const cv::Mat &plain, std::vector<cv::Mat> &out){
    out.resize(plain.rows);

    for(int i = 0; i < plain.rows; ++i)
    {
        out[i] = plain.row(i);
    }
}
void allController::testVocCreation(const std::vector<std::vector<cv::Mat > > &features){
    // branching factor and depth levels
    const int k = 9;
    const int L = 3;
    const WeightingType weight = TF_IDF;
    const ScoringType score = L1_NORM;

    OrbVocabulary voc(k, L, weight, score);
    voc.create(features);

    LOGE("===Matching images against themselves (0 low, 1 high): ");
    BowVector v1, v2;
    for(int i = 0; i < voc_images.size(); i++)
    {
        voc.transform(features[i], v1);
        for(int j = 0; j <  voc_images.size(); j++)
        {
            voc.transform(features[j], v2);

            double score = voc.score(v1, v2);
            LOGE("===Image %d vs Image %d score: %d", i, j, score);
        }
    }
    std::string fhead(getenv("CALVR_HOME"));
    std::string filename = fhead + "lapras_small_voc.yml.gz";
    voc.save(filename);
    LOGE("============================================saved==============");
//    CopyBackFiles(filename.c_str());
}
void allController::testDatabase(const std::vector<std::vector<cv::Mat > > &features){
    //test a dataset
    // load the vocabulary from disk
    std::string fhead(getenv("CALVR_HOME"));
    std::string filename = fhead + "lapras_small_voc.yml.gz";

    OrbVocabulary voc(filename);
    OrbDatabase db(voc, false, 0); // false = do not use direct index
    // (so ignore the last param)
    // The direct index is useful if we want to retrieve the features that
    // belong to some vocabulary node.
    // db creates a copy of the vocabulary, we may get rid of "voc" now
    // add images to the database
    for(int i = 0; i < features.size(); i++)
    {
        db.add(features[i]);
    }
    QueryResults ret;
    for(int i = 0; i < features.size(); i++)
    {
        db.query(features[i], ret, 4);

        // ret[0] is always the same image in this case, because we added it to the
        // database. ret[1] is the second best match.

        LOGE("===searching for image: %d , score %d",i,ret[1].Score);
    }
}

void allController::constructVOC() {
    std::vector<std::vector<cv::Mat > > features;
    loadFeatures(features);

    testVocCreation(features);
    testDatabase(features);
}