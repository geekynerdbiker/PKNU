import numpy as np
import pandas as pd
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.layers.experimental import preprocessing
import matplotlib.pyplot as plt


def build_and_compile_model(norm):
    model = keras.Sequential([
        norm,
        layers.Dense(64, activation='relu'),
        layers.Dense(64, activation='relu'),
        layers.Dense(1)
    ])

    model.compile(loss='mean_absolute_error',
                  optimizer=tf.keras.optimizers.Adam(0.001))
    return model


raw_dataset = pd.read_csv('insurance.csv', sep=',', header=0, skipinitialspace=True, na_values='?')
raw_dataset.pop("region")

dataset = raw_dataset.copy()
dataset = pd.get_dummies(dataset, prefix='', prefix_sep='')

train_dataset = dataset.sample(frac=0.8, random_state=0)
test_dataset = dataset.drop(train_dataset.index)

train_features = train_dataset.copy()
test_features = test_dataset.copy()
train_labels = train_features.pop('charges')
test_labels = test_features.pop('charges')

train_mean = train_features.mean(axis=0)
train_std = train_features.std(axis=0)

normalizer = preprocessing.Normalization()
normalizer.adapt(np.array(train_features))

dnn_model = build_and_compile_model(normalizer)

history = dnn_model.fit(
    train_features, train_labels,
    validation_split=0.2,
    verbose=0, epochs=100)

test_results = dnn_model.evaluate(test_features, test_labels, verbose=0)
print(test_results)