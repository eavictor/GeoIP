package com.eavictor.model.update;

public interface UpdateMethods {

	boolean dayCount();

	boolean checkDataExist();

	boolean setRealPath(String path);

	boolean downloadZip();

	boolean unZip();

	boolean doUpdate();

}