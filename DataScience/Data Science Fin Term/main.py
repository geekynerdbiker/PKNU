import tensorflow as tf
import matplotlib.pyplot as plt
import glob
from pathlib import Path


# Before run
# Remove under files from /images, /annotations/trimaps
# Remove line from /annotations/trainval.txt, /annotations/test.txt
# Abyssinian_5
# Egyptian_Mau_14, 138, 156, 186

def normalize(input_image, input_mask):
    input_image = tf.cast(input_image, tf.float32) / 255.0
    input_mask -= 1
    return input_image, input_mask


def load_image_train(img_path, mask_path):
    image = tf.io.read_file(img_path)
    image = tf.image.decode_jpeg(image)
    image = tf.image.resize(image, [128, 128])

    mask = tf.io.read_file(mask_path)
    mask = tf.io.decode_png(mask, channels=0, dtype=tf.dtypes.uint8)
    mask = tf.image.resize(mask, [128, 128])

    if tf.random.uniform(()) > 0.5:
        image = tf.image.flip_left_right(image)
        mask = tf.image.flip_left_right(mask)

    image, mask = normalize(image, mask)
    return image, mask


def load_image_test(img_path, mask_path):
    image = tf.io.read_file(img_path)
    image = tf.image.decode_jpeg(image)
    image = tf.image.resize(image, [128, 128])

    mask = tf.io.read_file(mask_path)
    mask = tf.io.decode_png(mask, channels=0, dtype=tf.dtypes.uint8)
    mask = tf.image.resize(mask, [128, 128])

    image, mask = normalize(image, mask)
    return image, mask


def display_images(display_list):
    plt.figure(figsize=(15, 15))

    title = ['Input Image', 'True Mask', 'Predicted Mask']

    for i in range(len(display_list)):
        plt.subplot(1, len(display_list), i + 1)
        plt.title(title[i])
        plt.imshow(tf.keras.preprocessing.image.array_to_img(display_list[i]))
        plt.axis('off')
    plt.show()


def downsample(filters, size, apply_batchnorm=True):
    initializer = tf.random_normal_initializer(0., 0.02)

    result = tf.keras.Sequential()
    result.add(
        tf.keras.layers.Conv2D(filters, size, strides=2, padding='same',
                               kernel_initializer=initializer, use_bias=False))

    if apply_batchnorm:
        result.add(tf.keras.layers.BatchNormalization())

    result.add(tf.keras.layers.LeakyReLU())

    return result


def upsample(filters, size, apply_dropout=False):
    initializer = tf.random_normal_initializer(0., 0.02)

    result = tf.keras.Sequential()
    result.add(
        tf.keras.layers.Conv2DTranspose(filters, size, strides=2,
                                        padding='same',
                                        kernel_initializer=initializer, use_bias=False))

    result.add(tf.keras.layers.BatchNormalization())

    if apply_dropout:
        result.add(tf.keras.layers.Dropout(0.5))

    result.add(tf.keras.layers.ReLU())
    return result


def unet_model(output_channels):
    inputs = tf.keras.layers.Input(shape=[128, 128, 3])
    x = inputs

    skips = []
    for down in down_stack:
        x = down(x)
        skips.append(x)

    skips = reversed(skips[:-1])

    for up, skip in zip(up_stack, skips):
        x = up(x)
        concat = tf.keras.layers.Concatenate()
        x = concat([x, skip])

    last = tf.keras.layers.Conv2DTranspose(
        output_channels, 3, strides=2,
        padding='same')  # 64x64 -> 128x128

    x = last(x)

    return tf.keras.Model(inputs=inputs, outputs=x)


def create_mask(pred_mask):
    pred_mask = tf.argmax(pred_mask, axis=-1)
    pred_mask = pred_mask[..., tf.newaxis]
    return pred_mask[0]


dataset_dir = 'Oxford-IIIT_Pet_Dataset'
image_paths = glob.glob('{}/images/*.jpg'.format(dataset_dir))
label_paths = glob.glob('{}/annotations/trimaps/*.png'.format(dataset_dir))
image_paths.sort()
label_paths.sort()

trainval_list = open('{}/annotations/trainval.txt'.format(dataset_dir), 'r')
Lines = trainval_list.readlines()
train_data_file_name = [line.split()[0] for line in Lines]

test_list = open('{}/annotations/test.txt'.format(dataset_dir), 'r')
Lines2 = test_list.readlines()
test_data_file_name = [line.split()[0] for line in Lines2]

trainval_image_paths = [p for p in image_paths if Path(p).stem in train_data_file_name]
trainval_label_paths = [p for p in label_paths if Path(p).stem in train_data_file_name]

test_image_paths = [p for p in image_paths if Path(p).stem in test_data_file_name]
test_label_paths = [p for p in label_paths if Path(p).stem in test_data_file_name]

train_image_path_ds = tf.data.Dataset.from_tensor_slices(trainval_image_paths)
train_label_path_ds = tf.data.Dataset.from_tensor_slices(trainval_label_paths)

test_image_path_ds = tf.data.Dataset.from_tensor_slices(test_image_paths)
test_label_path_ds = tf.data.Dataset.from_tensor_slices(test_label_paths)

train_dataset = tf.data.Dataset.zip((train_image_path_ds, train_label_path_ds))
test_dataset = tf.data.Dataset.zip((test_image_path_ds, test_label_path_ds))

train_ds = train_dataset.map(load_image_train)
test_ds = test_dataset.map(load_image_test)

TRAIN_LENGTH = len(trainval_image_paths)
BATCH_SIZE = 64
BUFFER_SIZE = 1000
STEPS_PER_EPOCH = TRAIN_LENGTH // BATCH_SIZE

train_dataset = train_ds.shuffle(BUFFER_SIZE).batch(BATCH_SIZE)
test_dataset = test_ds.batch(BATCH_SIZE)

OUTPUT_CHANNELS = 3

down_stack = [
    downsample(64, 3, apply_batchnorm=False),
    downsample(128, 3),
    downsample(256, 3),
    downsample(512, 3),
    downsample(512, 3),
]

up_stack = [
    upsample(512, 3),
    upsample(256, 3),
    upsample(128, 3),
    upsample(64, 3),
]

model = unet_model(OUTPUT_CHANNELS)
model.compile(optimizer='adam',
              loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              metrics=['accuracy'])

tf.keras.utils.plot_model(model, show_shapes=True)

EPOCHS = 20
TEST_LENGTH = len(test_image_paths)
VALIDATION_STEPS = TEST_LENGTH // BATCH_SIZE

model_history = model.fit(train_dataset, epochs=EPOCHS,
                          validation_steps=VALIDATION_STEPS,
                          validation_data=test_dataset, )

loss = model_history.history['loss']
val_loss = model_history.history['val_loss']
epochs = range(EPOCHS)