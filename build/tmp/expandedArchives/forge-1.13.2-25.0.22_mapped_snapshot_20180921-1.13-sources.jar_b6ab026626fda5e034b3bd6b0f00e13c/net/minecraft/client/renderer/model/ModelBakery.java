package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
   public static final ResourceLocation LOCATION_FIRE_0 = new ResourceLocation("block/fire_0");
   public static final ResourceLocation LOCATION_FIRE_1 = new ResourceLocation("block/fire_1");
   public static final ResourceLocation LOCATION_LAVA_FLOW = new ResourceLocation("block/lava_flow");
   public static final ResourceLocation LOCATION_WATER_FLOW = new ResourceLocation("block/water_flow");
   public static final ResourceLocation LOCATION_WATER_OVERLAY = new ResourceLocation("block/water_overlay");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_0 = new ResourceLocation("block/destroy_stage_0");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_1 = new ResourceLocation("block/destroy_stage_1");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_2 = new ResourceLocation("block/destroy_stage_2");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_3 = new ResourceLocation("block/destroy_stage_3");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_4 = new ResourceLocation("block/destroy_stage_4");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_5 = new ResourceLocation("block/destroy_stage_5");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_6 = new ResourceLocation("block/destroy_stage_6");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_7 = new ResourceLocation("block/destroy_stage_7");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_8 = new ResourceLocation("block/destroy_stage_8");
   public static final ResourceLocation LOCATION_DESTROY_STAGE_9 = new ResourceLocation("block/destroy_stage_9");
   protected static final Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(LOCATION_WATER_FLOW, LOCATION_LAVA_FLOW, LOCATION_WATER_OVERLAY, LOCATION_FIRE_0, LOCATION_FIRE_1, LOCATION_DESTROY_STAGE_0, LOCATION_DESTROY_STAGE_1, LOCATION_DESTROY_STAGE_2, LOCATION_DESTROY_STAGE_3, LOCATION_DESTROY_STAGE_4, LOCATION_DESTROY_STAGE_5, LOCATION_DESTROY_STAGE_6, LOCATION_DESTROY_STAGE_7, LOCATION_DESTROY_STAGE_8, LOCATION_DESTROY_STAGE_9, new ResourceLocation("item/empty_armor_slot_helmet"), new ResourceLocation("item/empty_armor_slot_chestplate"), new ResourceLocation("item/empty_armor_slot_leggings"), new ResourceLocation("item/empty_armor_slot_boots"), new ResourceLocation("item/empty_armor_slot_shield"));
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureSprite.getSprite().getName().getPath() + "',       'missingno': '" + MissingTextureSprite.getSprite().getName().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
   private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
   private static final Splitter SPLITTER_COMMA = Splitter.on(',');
   private static final Splitter EQUALS_SPLITTER = Splitter.on('=').limit(2);
   protected static final ModelBlock MODEL_GENERATED = Util.make(ModelBlock.deserialize("{}"), (p_209273_0_) -> {
      p_209273_0_.name = "generation marker";
   });
   protected static final ModelBlock MODEL_ENTITY = Util.make(ModelBlock.deserialize("{}"), (p_209274_0_) -> {
      p_209274_0_.name = "block entity marker";
   });
   private static final StateContainer<Block, IBlockState> STATE_CONTAINER_ITEM_FRAME = (new StateContainer.Builder<Block, IBlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).create(BlockState::new);
   protected final IResourceManager resourceManager;
   protected final TextureMap textureMap;
   protected final Map<ModelResourceLocation, IBakedModel> bakedRegistry = Maps.newHashMap();
   private static final Map<ResourceLocation, StateContainer<Block, IBlockState>> field_209607_C = ImmutableMap.of(new ResourceLocation("item_frame"), STATE_CONTAINER_ITEM_FRAME);
   private final Map<ResourceLocation, IUnbakedModel> unbakedModels = Maps.newHashMap();
   private final Set<ResourceLocation> unbakedModelLoadingQueue = Sets.newHashSet();
   private final ModelBlockDefinition.ContainerHolder containerHolder = new ModelBlockDefinition.ContainerHolder();

   public ModelBakery(IResourceManager resourceManagerIn, TextureMap textureMapIn) {
      this.resourceManager = resourceManagerIn;
      this.textureMap = textureMapIn;
   }

   private static Predicate<IBlockState> parseVariantKey(StateContainer<Block, IBlockState> containerIn, String variantIn) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap();

      for(String s : SPLITTER_COMMA.split(variantIn)) {
         Iterator<String> iterator = EQUALS_SPLITTER.split(s).iterator();
         if (iterator.hasNext()) {
            String s1 = iterator.next();
            IProperty<?> iproperty = containerIn.getProperty(s1);
            if (iproperty != null && iterator.hasNext()) {
               String s2 = iterator.next();
               Comparable<?> comparable = parseValue(iproperty, s2);
               if (comparable == null) {
                  throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + iproperty.getAllowedValues());
               }

               map.put(iproperty, comparable);
            } else if (!s1.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
            }
         }
      }

      Block block = containerIn.getOwner();
      return (p_209606_2_) -> {
         if (p_209606_2_ != null && block == p_209606_2_.getBlock()) {
            for(Entry<IProperty<?>, Comparable<?>> entry : map.entrySet()) {
               if (!Objects.equals(p_209606_2_.get(entry.getKey()), entry.getValue())) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      };
   }

   @Nullable
   static <T extends Comparable<T>> T parseValue(IProperty<T> property, String value) {
      return (T)(property.parseValue(value).orElse((T)null));
   }

   public IUnbakedModel getUnbakedModel(ResourceLocation modelLocation) {
      if (this.unbakedModels.containsKey(modelLocation)) {
         return this.unbakedModels.get(modelLocation);
      } else if (this.unbakedModelLoadingQueue.contains(modelLocation)) {
         throw new IllegalStateException("Circular reference while loading " + modelLocation);
      } else {
         this.unbakedModelLoadingQueue.add(modelLocation);
         IUnbakedModel iunbakedmodel = this.unbakedModels.get(MODEL_MISSING);

         while(!this.unbakedModelLoadingQueue.isEmpty()) {
            ResourceLocation resourcelocation = this.unbakedModelLoadingQueue.iterator().next();

            try {
               if (!this.unbakedModels.containsKey(resourcelocation)) {
                  this.loadBlockstate(resourcelocation);
               }
            } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
               LOGGER.warn(modelbakery$blockstatedefinitionexception.getMessage());
               this.unbakedModels.put(resourcelocation, iunbakedmodel);
            } catch (Exception exception) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, modelLocation, exception);
               this.unbakedModels.put(resourcelocation, iunbakedmodel);
            } finally {
               this.unbakedModelLoadingQueue.remove(resourcelocation);
            }
         }

         return this.unbakedModels.getOrDefault(modelLocation, iunbakedmodel);
      }
   }

   private void loadBlockstate(ResourceLocation p_209598_1_) throws Exception {
      if (!(p_209598_1_ instanceof ModelResourceLocation)) {
         this.putModel(p_209598_1_, this.loadModel(p_209598_1_));
      } else {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_209598_1_;
         if (Objects.equals(modelresourcelocation.getVariant(), "inventory")) {
            ResourceLocation resourcelocation2 = new ResourceLocation(p_209598_1_.getNamespace(), "item/" + p_209598_1_.getPath());
            ModelBlock modelblock = this.loadModel(resourcelocation2);
            this.putModel(modelresourcelocation, modelblock);
            this.unbakedModels.put(resourcelocation2, modelblock);
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(p_209598_1_.getNamespace(), p_209598_1_.getPath());
            StateContainer<Block, IBlockState> statecontainer = Optional.ofNullable(field_209607_C.get(resourcelocation)).orElseGet(() -> {
               return IRegistry.field_212618_g.get(resourcelocation).getStateContainer();
            });
            this.containerHolder.setStateContainer(statecontainer);
            ImmutableList<IBlockState> immutablelist = statecontainer.getValidStates();
            Map<ModelResourceLocation, IBlockState> map = Maps.newHashMap();
            immutablelist.forEach((p_209587_2_) -> {
               IBlockState iblockstate = map.put(BlockModelShapes.getModelLocation(resourcelocation, p_209587_2_), p_209587_2_);
            });
            Map<IBlockState, IUnbakedModel> map1 = Maps.newHashMap();
            ResourceLocation resourcelocation1 = new ResourceLocation(p_209598_1_.getNamespace(), "blockstates/" + p_209598_1_.getPath() + ".json");
            boolean flag = false;

            label160: {
               try {
                  label161: {
                     List<Pair<String, ModelBlockDefinition>> lvt_9_4_;
                     try {
                        flag = true;
                        lvt_9_4_ = this.resourceManager.getAllResources(resourcelocation1).stream().map((p_209591_1_) -> {
                           try (InputStream inputstream = p_209591_1_.getInputStream()) {
                              return Pair.of(p_209591_1_.getPackName(), ModelBlockDefinition.fromJson(this.containerHolder, new InputStreamReader(inputstream, StandardCharsets.UTF_8), p_209598_1_));
                           } catch (Exception exception1) {
                              throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", p_209591_1_.getLocation(), p_209591_1_.getPackName(), exception1.getMessage()));
                           }
                        }).collect(Collectors.toList());
                     } catch (IOException ioexception) {
                        LOGGER.warn("Exception loading blockstate definition: {}: {}", resourcelocation1, ioexception);
                        flag = false;
                        break label161;
                     }

                     for(Pair<String, ModelBlockDefinition> pair : lvt_9_4_) {
                        ModelBlockDefinition modelblockdefinition = pair.getSecond();
                        Map<IBlockState, IUnbakedModel> map2 = Maps.newIdentityHashMap();
                        IUnbakedModel iunbakedmodel;
                        if (modelblockdefinition.hasMultipartData()) {
                           iunbakedmodel = modelblockdefinition.getMultipartData();
                           immutablelist.forEach((p_209603_2_) -> {
                              IUnbakedModel iunbakedmodel1 = map2.put(p_209603_2_, iunbakedmodel);
                           });
                        } else {
                           iunbakedmodel = null;
                        }

                        modelblockdefinition.func_209578_a().forEach((p_209589_8_, p_209589_9_) -> {
                           try {
                              immutablelist.stream().filter(parseVariantKey(statecontainer, p_209589_8_)).forEach((p_209590_5_) -> {
                                 IUnbakedModel iunbakedmodel1 = map2.put(p_209590_5_, p_209589_9_);
                                 if (iunbakedmodel1 != null && iunbakedmodel1 != iunbakedmodel) {
                                    map2.put(p_209590_5_, this.unbakedModels.get(MODEL_MISSING));
                                    throw new RuntimeException("Overlapping definition with: " + (String)modelblockdefinition.func_209578_a().entrySet().stream().filter((p_209604_1_) -> {
                                       return p_209604_1_.getValue() == iunbakedmodel1;
                                    }).findFirst().get().getKey());
                                 }
                              });
                           } catch (Exception exception1) {
                              LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair.getFirst(), p_209589_8_, exception1.getMessage());
                           }

                        });
                        map1.putAll(map2);
                     }

                     flag = false;
                     break label160;
                  }
               } catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception) {
                  throw modelbakery$blockstatedefinitionexception;
               } catch (Exception exception) {
                  throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourcelocation1, exception));
               } finally {
                  if (flag) {
                     for(Entry<ModelResourceLocation, IBlockState> entry : map.entrySet()) {
                        this.putModel(entry.getKey(), map1.getOrDefault(entry.getValue(), this.unbakedModels.get(MODEL_MISSING)));
                     }

                  }
               }

               for(Entry<ModelResourceLocation, IBlockState> entry2 : map.entrySet()) {
                  this.putModel(entry2.getKey(), map1.getOrDefault(entry2.getValue(), this.unbakedModels.get(MODEL_MISSING)));
               }

               return;
            }

            for(Entry<ModelResourceLocation, IBlockState> entry1 : map.entrySet()) {
               this.putModel(entry1.getKey(), map1.getOrDefault(entry1.getValue(), this.unbakedModels.get(MODEL_MISSING)));
            }
         }

      }
   }

   private void putModel(ResourceLocation p_209593_1_, IUnbakedModel p_209593_2_) {
      this.unbakedModels.put(p_209593_1_, p_209593_2_);
      this.unbakedModelLoadingQueue.addAll(p_209593_2_.getOverrideLocations());
   }

   private void getUnbakedModel(Map<ModelResourceLocation, IUnbakedModel> p_209594_1_, ModelResourceLocation p_209594_2_) {
      p_209594_1_.put(p_209594_2_, this.getUnbakedModel(p_209594_2_));
   }

   public Map<ModelResourceLocation, IBakedModel> setupModelRegistry() {
      Map<ModelResourceLocation, IUnbakedModel> map = Maps.newHashMap();

      try {
         this.unbakedModels.put(MODEL_MISSING, this.loadModel(MODEL_MISSING));
         this.getUnbakedModel(map, MODEL_MISSING);
      } catch (IOException ioexception) {
         LOGGER.error("Error loading missing model, should never happen :(", (Throwable)ioexception);
         throw new RuntimeException(ioexception);
      }

      field_209607_C.forEach((p_209602_2_, p_209602_3_) -> {
         p_209602_3_.getValidStates().forEach((p_209601_3_) -> {
            this.getUnbakedModel(map, BlockModelShapes.getModelLocation(p_209602_2_, p_209601_3_));
         });
      });

      for(Block block : IRegistry.field_212618_g) {
         block.getStateContainer().getValidStates().forEach((p_209600_2_) -> {
            this.getUnbakedModel(map, BlockModelShapes.getModelLocation(p_209600_2_));
         });
      }

      for(ResourceLocation resourcelocation : IRegistry.field_212630_s.getKeys()) {
         this.getUnbakedModel(map, new ModelResourceLocation(resourcelocation, "inventory"));
      }

      this.getUnbakedModel(map, new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      Set<String> set = Sets.newLinkedHashSet();
      Set<ResourceLocation> set1 = map.values().stream().flatMap((p_209595_2_) -> {
         return p_209595_2_.getTextures(this::getUnbakedModel, set).stream();
      }).collect(Collectors.toSet());
      set1.addAll(LOCATIONS_BUILTIN_TEXTURES);
      set.forEach((p_209588_0_) -> {
         LOGGER.warn("Unable to resolve texture reference: {}", (Object)p_209588_0_);
      });
      this.textureMap.stitch(this.resourceManager, set1);
      map.forEach((p_209599_1_, p_209599_2_) -> {
         IBakedModel ibakedmodel = null;

         try {
            ibakedmodel = p_209599_2_.bake(this::getUnbakedModel, this.textureMap::getSprite, ModelRotation.X0_Y0, false);
         } catch (Exception exception) {
            LOGGER.warn("Unable to bake model: '{}': {}", p_209599_1_, exception);
         }

         if (ibakedmodel != null) {
            this.bakedRegistry.put(p_209599_1_, ibakedmodel);
         }

      });
      return this.bakedRegistry;
   }

   protected ModelBlock loadModel(ResourceLocation location) throws IOException {
      Reader reader = null;
      IResource iresource = null;

      ModelBlock lvt_5_2_;
      try {
         String s = location.getPath();
         if (!"builtin/generated".equals(s)) {
            if ("builtin/entity".equals(s)) {
               lvt_5_2_ = MODEL_ENTITY;
               return lvt_5_2_;
            }

            if (s.startsWith("builtin/")) {
               String s2 = s.substring("builtin/".length());
               String s1 = BUILT_IN_MODELS.get(s2);
               if (s1 == null) {
                  throw new FileNotFoundException(location.toString());
               }

               reader = new StringReader(s1);
            } else {
               iresource = this.resourceManager.getResource(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json"));
               reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
            }

            lvt_5_2_ = ModelBlock.deserialize(reader);
            lvt_5_2_.name = location.toString();
            ModelBlock modelblock1 = lvt_5_2_;
            return modelblock1;
         }

         lvt_5_2_ = MODEL_GENERATED;
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly((Closeable)iresource);
      }

      return lvt_5_2_;
   }

   @OnlyIn(Dist.CLIENT)
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String message) {
         super(message);
      }
   }
}