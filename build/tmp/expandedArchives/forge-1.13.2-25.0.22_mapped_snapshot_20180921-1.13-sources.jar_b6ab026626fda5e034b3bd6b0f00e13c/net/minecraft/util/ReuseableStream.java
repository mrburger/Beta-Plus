package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReuseableStream<T> {
   private final List<T> field_212762_a = Lists.newArrayList();
   private final Iterator<T> field_212763_b;

   public ReuseableStream(Stream<T> p_i49816_1_) {
      this.field_212763_b = p_i49816_1_.iterator();
   }

   public Stream<T> func_212761_a() {
      return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
         private int field_212758_b = 0;

         public boolean tryAdvance(Consumer<? super T> p_tryAdvance_1_) {
            T t;
            if (this.field_212758_b >= ReuseableStream.this.field_212762_a.size()) {
               if (!ReuseableStream.this.field_212763_b.hasNext()) {
                  return false;
               }

               t = ReuseableStream.this.field_212763_b.next();
               ReuseableStream.this.field_212762_a.add(t);
            } else {
               t = ReuseableStream.this.field_212762_a.get(this.field_212758_b);
            }

            ++this.field_212758_b;
            p_tryAdvance_1_.accept(t);
            return true;
         }
      }, false);
   }
}