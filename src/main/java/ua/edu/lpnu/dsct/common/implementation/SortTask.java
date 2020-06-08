package ua.edu.lpnu.dsct.common.implementation;

import ua.edu.lpnu.dsct.common.abstraction.ITask;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SortTask implements ITask<byte[]>, Serializable {
    byte[] byteArray;
    int number;    //////////////////////////////////////////

    public SortTask(byte[] byteArray, int number) {
        this.byteArray = byteArray;
        this.number = number;
    }

    public int [] sort(int [] array) {
        int size = array.length;
        for(int i = 1; i < size; ++i) {
            for(int j = i; j > 0 && array[j-1] > array[j]; --j) {
                int tmp = array[j];
                array[j] = array[j-1];
                array[j-1] = tmp;
            }
        }
        return array;
    }

    public int searchNumber(int searchNumber, int [] array ){
        int position; // для подсчета количества сравнений
        int first = 0;
        int last = array.length - 1;


        // для начала найдем индекс среднего элемента массива
        position = (first + last) / 2;

        while ((array[position] != searchNumber) && (first <= last)) {

            if (array[position] > searchNumber) {  // если число заданного для поиска
                last = position - 1; // уменьшаем позицию на 1.
            } else {
                first = position + 1;    // иначе увеличиваем на 1
            }
            position = (first + last) / 2;
        }
        if (first <= last) {
            return position;
        } else {
            return -1;
        }


    }


    @Override
    public byte[] execute() {
        String delimiter = " ";

        String content = new String(this.byteArray);
        int searchedNumber = this.number;   ///////////////////////////////////

        String[] strArray = content.replace(System.lineSeparator(), delimiter).split(delimiter);
        int [] numberArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();

        int[] resultArray = this.sort(numberArray);
        int resultIndex = this.searchNumber(searchedNumber, resultArray);

        if(resultIndex == -1) {

            String strResult = "number did not find in array";

            return strResult.getBytes();
        } else {
            String strResult = String.valueOf(resultIndex);
            return strResult.getBytes();
        }
    }
}
