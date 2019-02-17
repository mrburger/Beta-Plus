package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class FluidTagsProvider extends TagsProvider<Fluid> {
   public FluidTagsProvider(DataGenerator generatorIn) {
      super(generatorIn, IRegistry.field_212619_h);
   }

   protected void registerTags() {
      this.getBuilder(FluidTags.WATER).add(Fluids.WATER, Fluids.FLOWING_WATER);
      this.getBuilder(FluidTags.LAVA).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
   }

   /**
    * Resolves a Path for the location to save the given tag.
    *  
    * @param id ID of the tag
    */
   protected Path makePath(ResourceLocation id) {
      return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Fluid Tags";
   }

   protected void setCollection(TagCollection<Fluid> colectionIn) {
      FluidTags.setCollection(colectionIn);
   }
}