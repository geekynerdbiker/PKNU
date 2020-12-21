import os
import tensorflow as tf
from tensorflow import keras
import numpy as np
import glob
import cv2


def load_image(addr):
    img = cv2.imread(addr)
    img = cv2.resize(img, (128, 128), interpolation=cv2.INTER_CUBIC)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

    img = img.astype(np.float32)
    img = (img - np.mean(img)) / np.std(img)
    return img


def load_data():
    dataset_dir = 'Oxford-IIIT_Pet_Dataset'
    image_paths = glob.glob('{}/images/*.jpg'.format(dataset_dir))
    image_paths.sort()

    train_images = []
    train_labels = []
    test_images = []
    test_labels = []

    trainval_list = open('{}/annotations/trainval.txt'.format(dataset_dir), 'r')
    test_list = open('{}/annotations/test.txt'.format(dataset_dir), 'r')

    Lines = trainval_list.readlines()
    for line in Lines:
        train_images.append(load_image(os.path.join(dataset_dir, 'images', line.split()[0] + '.jpg')))
        train_labels.append(float(line.split()[2]) - 1)

    Lines2 = test_list.readlines()
    for line in Lines2:
        test_images.append(load_image(os.path.join(dataset_dir, 'images', line.split()[0] + '.jpg')))
        test_labels.append(float(line.split()[2]) - 1)

    idxs = np.arange(0, len(train_images))
    np.random.shuffle(idxs)
    train_images = np.array(train_images)
    train_labels = np.array(train_labels)
    test_images = np.array(test_images)
    test_labels = np.array(test_labels)

    return train_images, train_labels, test_images, test_labels


def create_model():
    model = keras.Sequential([
        keras.layers.Conv2D(32, kernel_size=3, activation='relu', padding='same', input_shape=(128, 128, 3)),
        keras.layers.MaxPool2D(pool_size=(2, 2), strides=(2, 2), padding='same'),
        keras.layers.Conv2D(64, kernel_size=3, activation='relu', padding='same'),
        keras.layers.MaxPool2D(pool_size=(2, 2), strides=(2, 2), padding='same'),
        keras.layers.Conv2D(128, kernel_size=3, activation='relu', padding='same'),
        keras.layers.MaxPool2D(pool_size=(2, 2), strides=(2, 2), padding='same'),
        keras.layers.Flatten(),
        keras.layers.Dense(512, activation=tf.nn.relu), keras.layers.Dropout(0.2),
        keras.layers.Dense(2, activation=tf.nn.softmax)
    ])

    model.compile(optimizer='adam', loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
                  metrics=['accuracy'])

    model.summary()
    return model


def train(model, train_features, train_labels, val_features, val_labels):
    model.fit(train_features, train_labels, epochs=50, validation_data=(val_features, val_labels))


def train_from_scratch():
    class_names = ['cat', 'dog']
    train_features, train_labels, test_features, test_labels = load_data()
    model = create_model()
    model.fit(train_features, train_labels, epochs=50, validation_data=(test_features, test_labels))
    test_loss, test_acc = model.evaluate(test_features, test_labels)

    print('Test accuracy:', test_acc)
    predictions = model.predict(test_features)
    print(predictions[0])
    print(np.argmax(predictions[0]))
    print(test_labels[0])


if __name__ == '__main__':
    train_from_scratch()
