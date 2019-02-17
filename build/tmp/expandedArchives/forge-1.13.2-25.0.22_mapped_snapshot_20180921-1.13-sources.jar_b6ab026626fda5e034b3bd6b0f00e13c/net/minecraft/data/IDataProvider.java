package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.IOException;

public interface IDataProvider {
   /**
    * Hash function to be used for caching. In most cases, this should be done with the {@link
    * HashFunction#hashUnencodedChars} function.
    */
   HashFunction HASH_FUNCTION = Hashing.sha1();

   /**
    * Performs this provider's action.
    */
   void act(DirectoryCache cache) throws IOException;

   /**
    * Gets a name for this provider, to use in logging.
    */
   String getName();
}