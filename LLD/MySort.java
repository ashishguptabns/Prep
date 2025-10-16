package LLD;

/* 
quick sort
top-down merge sort
bottom-up merge sort
heap sort
selection sort
insertion sort
bubble sort (TLE)
 */
public class MySort {
    private int[] quickSortHelper(int[] nums, int start, int end) {
        if (start >= end) {
            return new int[] {};
        }
        int pivot = end;
        int left = start;
        for (int i = start; i <= end; i++) {
            if (nums[i] < nums[pivot]) {
                int temp = nums[left];
                nums[left] = nums[i];
                nums[i] = temp;
                left++;
            }
        }
        int temp = nums[left];
        nums[left] = nums[pivot];
        nums[pivot] = temp;

        int[] leftArr = quickSortHelper(nums, start, left - 1);
        int[] rightArr = quickSortHelper(nums, left + 1, end);

        for (int i = start; i < left - 1; i++) {
            nums[i] = leftArr[i];
        }
        for (int i = left + 1; i < end; i++) {
            nums[i] = rightArr[i];
        }

        return nums;
    }

    private int[] quickSort(int[] nums) {
        if (nums.length == 1) {
            return nums;
        }
        return quickSortHelper(nums, 0, nums.length - 1);
    }

    public static void main(String[] ar) {
        MySort app = new MySort();
        int[] nums = new int[] { 1, 4, 90, 2, 7, 1001, 3 };
        for (int n : app.quickSort(nums)) {
            System.out.print(n + ", ");
        }
        System.out.println();
        nums = new int[] { 1, 4, 90, 2, 7, 1001, 3 };
        for (int n : app.bubbleSort(nums)) {
            System.out.print(n + ", ");
        }
        System.out.println();
        nums = new int[] { 1, 4, 90, 2, 7, 1001, 3 };
        for (int n : app.insertionSort(nums)) {
            System.out.print(n + ", ");
        }
        System.out.println();
    }

    private int[] insertionSort(int[] nums) {
        return nums;
    }

    private int[] bubbleSort(int[] nums) {
        int right = nums.length - 1;
        while (right >= 0) {
            for (int i = 0; i <= right; i++) {
                if (nums[i] > nums[right]) {
                    int temp = nums[i];
                    nums[i] = nums[right];
                    nums[right] = temp;
                }
            }
            right--;
        }
        return nums;
    }
}
