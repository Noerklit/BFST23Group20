package KDTree;

import Model.Way;

import java.util.List;


public class Quickselect {



//    private static int partitionBad(List<Way> list, int left, int right, int pivotIndex, boolean isX){
//        double pivotValue;
//        if (isX){
//            pivotValue = list.get(pivotIndex).xVal;
//        } else {
//            pivotValue = list.get(pivotIndex).yVal;
//        }
//
//        swap(list, pivotIndex, right);
//
//        int storeIndex = left;
//        for (int i = left; i<right; i++){
//            if (isX){
//                if (list.get(i).xVal < pivotValue){
//                    swap(list, storeIndex, i);
//                    storeIndex++;
//                }
//            } else {
//                if (list.get(i).yVal < pivotValue){
//                    swap(list, storeIndex, i);
//                    storeIndex++;
//                }
//            }
//        }
//        swap(list, right, storeIndex);
//        return storeIndex;
//    }
    private static int partition(Way[] list, int left, int right, int pivotIndex, boolean isX){
        var pivot = list[left + (int)(Math.random()%(right-left+1))].xVal;
        int i = left;
        int j = right+1;

        while (true){
            while (list[++i].xVal<pivot){
                if (i == right)
                    break;
            }
            while(list[--j].xVal>pivot){
                if (j == left)
                    break;
            }
            if(i>=j){
                break;
            }
            swap(list, i, j);
        }
        swap(list, left, j);
        return j;
    }

    public static Way select(Way[] list, int left, int right, int k, boolean isX){
        int pivotIndex;
        if (left == right){
            return list[left];
        }

//        pivotIndex = left + (int)(Math.random() % (right - left +1));
//        pivotIndex = partition(list, left, right, pivotIndex, isX);
//
//        if (k == pivotIndex){
//            System.out.println(k);
//            return list.get(k);
//        } else if (k < pivotIndex) {
//            return select(list, left, pivotIndex - 1, k, isX);
//        } else {
//            return select(list, pivotIndex + 1, right, k, isX);
//        }
        while (right>left){
            int i = partition(list,left,right,0,true);
            if(i>k) {
                right = i - 1;
            }
            else if (i<k) {
                left = i+1;
            }
            else {
                return list[i];
            }
        }
        return list[left];
    }

    private static void swap(Way[] list, int val1, int val2){
        Way temp = list[val1];
        list[val1] = list[val2];
        list[val2] = temp;
    }
}
