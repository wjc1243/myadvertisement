package com.wjc.ad.mysql.listener;

public class Test {
    public static void main(String[] args) {
        int[] nums = {1, 5, 2, 7, 13};

        for (int i = 2; i < nums.length; i++) {
            nums[i] = Math.max(nums[i-2] + nums[i], nums[i-1]);
        }
        System.out.println(nums[nums.length-1]);

        int[] ints = new int[10];
        ints[0] = 1;
        ints[1] = 2;
        for (int i = 2; i < ints.length; i++) {
            ints[i] = ints[i-2] + ints[i-1];
        }
        System.out.println(ints[ints.length-1]);
        System.out.println("共有" + compute(10) + "种走法");
    }

    static int compute(int stair){
        if ( stair <= 0){
            return 0;
        }
        if (stair == 1){
            return 1;
        }
        if (stair == 2){
            return 2;
        }
        return compute(stair-1) + compute(stair-2);
    }
}
