import numpy as np
from glob import glob
from os.path import basename
import joblib
from sklearn.ensemble import RandomForestClassifier
import csv

map_name = '' #Name of the space you're training the classifier on. This folder should contain CSV files
path = 'trainingDataFiles/' + map_name

dataset = None
classmap = {}

for class_idx, filename in enumerate(glob('%s/*.csv'%path)):
    class_name = basename(filename)[:-4]
    classmap[class_idx] = class_name
    samples = np.loadtxt(filename, dtype=float, delimiter = ',', skiprows = 1)
    labels = np.ones((len(samples),1)) * class_idx
    samples = np.hstack((samples, labels))
    dataset = samples if dataset is None else np.vstack((dataset, samples))

def get_classifier(features):
    X,y = features[:,:-1], features[:,-1]
    return RandomForestClassifier(20, max_depth=10).fit(X,y)

classifier = get_classifier(dataset)
save_path = path + '/' + map_name + ' classifier.joblib'
#print(save_path)
joblib.dump(classifier, save_path)
    
save_path_classmap = path + '/' + map_name + ' classmap.txt'
f = open(save_path_classmap, "w")
w = csv.writer(f)
for key, val in classmap.items():
    w.writerow([key, val])
f.close()


