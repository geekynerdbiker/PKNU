# Assignment 1-1
def insertion_sort(arr1):
    """
    Insertion Sort

    :param arr1: List(int)
    :return: List(int)
    """

    for i in range(1, len(arr1)):
        for j in range(i, 0, -1):
            if arr1[j - 1] > arr1[j]:
                arr1[j - 1], arr1[j] = arr1[j], arr1[j - 1]
    return arr1


# Assignment 1-2
def merge_sort(arr2):
    """
    Merge Sort

    :param arr2: List(int)
    :return: List(int)
    """

    if len(arr2) < 2:
        return arr2

    mid = len(arr2) // 2
    left = merge_sort(arr2[:mid])
    right = merge_sort(arr2[mid:])

    i, j, merged = 0, 0, []

    while i < len(left) and j < len(right):
        if left[i] < right[j]:
            merged.append(left[i])
            i += 1
        else:
            merged.append(right[j])
            j += 1

    merged += left[i:]
    merged += right[j:]
    return merged


# Assignment 2-1
def count_gas_vw_sedan(data1):
    """
    Number of gas sedans made by Volkswagen.

    :param data1: List(str)
    :return: int
    """

    count = 0

    print(type(data1))
    for i in data1:
        if i.find('Gas') >= 0 and i.find('Volkswagen') >= 0 and i.find('sedan') >= 0:
            count += 1

    return count


# Assignment 2-2
def order_and_print_bmws(data2):
    """
    Order by asc a BMW cars list and Print.

    :param data2: List(str)
    :return: None
    """

    bmws = []

    for i in data2:
        if i.find('BMW') >= 0:
            bmws.append(i.strip())

    for i in range(1, len(bmws)):
        for j in range(i, 0, -1):
            if bmws[j - 1].split(',')[7] > bmws[j].split(',')[7]:
                bmws[j - 1], bmws[j] = bmws[j], bmws[j - 1]

    for i in range(0, len(bmws)):
        print(bmws[i])

    print('\n')


# Assignment 3
def car_dictionary_list(head1, data3):
    """
    Car Dictionary List

    :param head1: str
    :param data3: List(str)
    :return: None
    """

    lst, hd = [], head1.split(',')

    for i in data3:
        dic = {}
        i = i.strip().split(',')
        for idx, j in enumerate(h):
            j = j.strip()
            dic[j] = i[idx].strip()
        lst.append(dic)


# Assignment 3
def print_specific_cars(data4):
    """
    Order by high price and Print gas sedans
    made after 2000 which is $ 20,000 ~ $ 50,000

    :param data4: List(str)
    :return: None
    """

    cars = []

    for i in data4:
        info = i.split(',')
        if info[1] != 'NA' and info[7] >= "2000":
            if i.find('sedan') >= 0 and i.find('Gas') >= 0:
                if 20000 <= float(info[1]) <= 50000:
                    cars.append(i.strip())

    for i in range(1, len(cars)):
        for j in range(i, 0, -1):
            if cars[j - 1].split(',')[1] < cars[j].split(',')[1]:
                cars[j - 1], cars[j] = cars[j], cars[j - 1]

    for i in range(0, len(cars)):
        print(cars[i])

    print('\n')


# Assignment 3
def print_brand_set(data5):
    """
    Print a set include all kind of Brand

    :param data5: List(str)
    :return: None
    """

    brands = {'Brand'}

    for i in data5:
        brand = i.split(',')[0]
        brands.add(brand)

    brands.remove('Brand')
    print(brands)


# Assignments
def assign1(arr):
    """
    Run Assignments 1-1, 1-2

    :param arr: List(int)
    :return: None
    """

    print(" ----------------- Assignment 1 -----------------\n")
    print(f'Result of Insertion Sort : {insertion_sort(arr)}')
    print(f'Result of Insertion Sort : {merge_sort(arr)}')


def assign2(data):
    """
    Run Assignment 2-1, 2-2

    :param data: List(str)
    :return: None
    """

    print(" ----------------- Assignment 2 -----------------\n")
    print(f'{count_gas_vw_sedan(data)}\n')
    order_and_print_bmws(data)


def assign3(head, data):
    """
    Run Assignment 3-1, 3-2, 3-3

    :param head: str
    :param data: List(str)
    :return: None
    """

    print(" ----------------- Assignment 3 -----------------\n")
    car_dictionary_list(head, data)
    print_specific_cars(data)
    print_brand_set(data)


# Main
if __name__ == "__main__":
    al = [3, 6, 8, 10, 1, 2, 1]
    f = open('/content/drive/My Drive/DataScience2020/cars.csv', 'r')
    h = f.readline()
    d = f.readlines()
    f.close()

    assign1(al)
    assign2(d)
    assign3(h, d)
