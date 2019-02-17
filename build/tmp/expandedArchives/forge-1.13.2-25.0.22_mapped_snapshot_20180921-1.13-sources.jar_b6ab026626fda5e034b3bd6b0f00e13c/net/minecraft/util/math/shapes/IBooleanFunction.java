package net.minecraft.util.math.shapes;

public interface IBooleanFunction {
   IBooleanFunction FALSE = (p_lambda$static$0_0_, p_lambda$static$0_1_) -> {
      return false;
   };
   IBooleanFunction NOT_OR = (p_lambda$static$1_0_, p_lambda$static$1_1_) -> {
      return !p_lambda$static$1_0_ && !p_lambda$static$1_1_;
   };
   IBooleanFunction ONLY_SECOND = (p_lambda$static$2_0_, p_lambda$static$2_1_) -> {
      return p_lambda$static$2_1_ && !p_lambda$static$2_0_;
   };
   IBooleanFunction NOT_FIRST = (p_lambda$static$3_0_, p_lambda$static$3_1_) -> {
      return !p_lambda$static$3_0_;
   };
   IBooleanFunction ONLY_FIRST = (p_lambda$static$4_0_, p_lambda$static$4_1_) -> {
      return p_lambda$static$4_0_ && !p_lambda$static$4_1_;
   };
   IBooleanFunction NOT_SECOND = (p_lambda$static$5_0_, p_lambda$static$5_1_) -> {
      return !p_lambda$static$5_1_;
   };
   IBooleanFunction NOT_SAME = (p_lambda$static$6_0_, p_lambda$static$6_1_) -> {
      return p_lambda$static$6_0_ != p_lambda$static$6_1_;
   };
   IBooleanFunction NOT_AND = (p_lambda$static$7_0_, p_lambda$static$7_1_) -> {
      return !p_lambda$static$7_0_ || !p_lambda$static$7_1_;
   };
   IBooleanFunction AND = (p_lambda$static$8_0_, p_lambda$static$8_1_) -> {
      return p_lambda$static$8_0_ && p_lambda$static$8_1_;
   };
   IBooleanFunction SAME = (p_lambda$static$9_0_, p_lambda$static$9_1_) -> {
      return p_lambda$static$9_0_ == p_lambda$static$9_1_;
   };
   IBooleanFunction SECOND = (p_lambda$static$10_0_, p_lambda$static$10_1_) -> {
      return p_lambda$static$10_1_;
   };
   IBooleanFunction CAUSES = (p_lambda$static$11_0_, p_lambda$static$11_1_) -> {
      return !p_lambda$static$11_0_ || p_lambda$static$11_1_;
   };
   IBooleanFunction FIRST = (p_lambda$static$12_0_, p_lambda$static$12_1_) -> {
      return p_lambda$static$12_0_;
   };
   IBooleanFunction CAUSED_BY = (p_lambda$static$13_0_, p_lambda$static$13_1_) -> {
      return p_lambda$static$13_0_ || !p_lambda$static$13_1_;
   };
   IBooleanFunction OR = (p_lambda$static$14_0_, p_lambda$static$14_1_) -> {
      return p_lambda$static$14_0_ || p_lambda$static$14_1_;
   };
   IBooleanFunction TRUE = (p_lambda$static$15_0_, p_lambda$static$15_1_) -> {
      return true;
   };

   boolean apply(boolean p_apply_1_, boolean p_apply_2_);
}