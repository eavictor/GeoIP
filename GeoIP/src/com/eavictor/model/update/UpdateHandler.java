package com.eavictor.model.update;

public class UpdateHandler {
	UpdateMethods updateMethods = new UpdateMethodsHibernate();

	public void setPath(String path) {
		updateMethods.setRealPath(path);
	}

	public void processUpdate() {
		while (true) {
			updateMethods.flushClientRequestCount();
			if (updateMethods.dayCount()) {
				if (updateMethods.downloadZip()) {
					if (updateMethods.unZip()) {
						if (updateMethods.doUpdate()) {
							System.out.println("Database update complete !!");
							try {
								Thread.sleep(86400000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				System.out.println("No update needed.");
				try {
					Thread.sleep(86400000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
