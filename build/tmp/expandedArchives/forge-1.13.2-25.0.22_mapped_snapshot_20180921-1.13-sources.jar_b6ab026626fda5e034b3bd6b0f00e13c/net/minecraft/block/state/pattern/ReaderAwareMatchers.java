package net.minecraft.block.state.pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public final class ReaderAwareMatchers {
   public static <T> IBlockMatcherReaderAware<T> not(IBlockMatcherReaderAware<T> matcher) {
      return new ReaderAwareMatchers.NotMatcher<>(matcher);
   }

   public static <T> IBlockMatcherReaderAware<T> or(IBlockMatcherReaderAware<? super T>... matchers) {
      return new ReaderAwareMatchers.OrMatcher<T>(toListAssertingNonNull(matchers));
   }

   private static <T> List<T> toListAssertingNonNull(T... array) {
      return toListAssertingNonNull(Arrays.asList(array));
   }

   private static <T> List<T> toListAssertingNonNull(Iterable<T> iterable) {
      List<T> list = Lists.newArrayList();

      for(T t : iterable) {
         list.add(Preconditions.checkNotNull(t));
      }

      return list;
   }

   static class NotMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final IBlockMatcherReaderAware<T> matcher;

      NotMatcher(IBlockMatcherReaderAware<T> matcherIn) {
         this.matcher = Preconditions.checkNotNull(matcherIn);
      }

      public boolean test(@Nullable T p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
         return !this.matcher.test(p_test_1_, p_test_2_, p_test_3_);
      }
   }

   static class OrMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final List<? extends IBlockMatcherReaderAware<? super T>> matchers;

      private OrMatcher(List<? extends IBlockMatcherReaderAware<? super T>> matchersIn) {
         this.matchers = matchersIn;
      }

      public boolean test(@Nullable T p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
         for(int i = 0; i < this.matchers.size(); ++i) {
            if (this.matchers.get(i).test(p_test_1_, p_test_2_, p_test_3_)) {
               return true;
            }
         }

         return false;
      }
   }
}