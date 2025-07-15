package guc.internship.modelscrapper.service.estimator;

import java.io.File;

public interface EstimatingStrategy{
     String getVolume(File stlFile);
     String getWeight(File stlFile,String material);
}