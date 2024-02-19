/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.neoforged.neoforge.client.rendererimpl;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.renderer.Renderer;
import net.neoforged.neoforge.client.renderer.RendererAccess;
import net.neoforged.neoforge.client.renderer.material.RenderMaterial;
import net.neoforged.neoforge.client.renderer.mesh.QuadEmitter;
import net.neoforged.neoforge.client.renderer.model.ModelHelper;
import net.neoforged.neoforge.client.renderer.render.RenderContext;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

/**
 * Routines for adaptation of vanilla {@link BakedModel}s to FRAPI pipelines.
 * Even though Indigo calls them directly, they are not for use by third party renderers, and might change at any time.
 */
public class VanillaModelEncoder {
    private static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
    private static final RenderMaterial MATERIAL_STANDARD = RENDERER.materialFinder().find();
    private static final RenderMaterial MATERIAL_NO_AO = RENDERER.materialFinder().ambientOcclusion(TriState.FALSE).find();

    // Separate QuadEmitter parameter so that Indigo can pass its own emitter that handles vanilla quads differently.
    public static void emitBlockQuads(BakedModel model, @Nullable BlockState state, Supplier<RandomSource> randomSupplier, RenderContext context, QuadEmitter emitter) {
        final RenderMaterial defaultMaterial = model.useAmbientOcclusion() ? MATERIAL_STANDARD : MATERIAL_NO_AO;

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);

            if (!context.hasTransform() && context.isFaceCulled(cullFace)) {
                // Skip entire quad list if possible.
                continue;
            }

            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());
            final int count = quads.size();

            for (int j = 0; j < count; j++) {
                final BakedQuad q = quads.get(j);
                emitter.fromVanilla(q, defaultMaterial, cullFace);
                emitter.emit();
            }
        }
    }

    public static void emitItemQuads(BakedModel model, @Nullable BlockState state, Supplier<RandomSource> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);
            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());
            final int count = quads.size();

            for (int j = 0; j < count; j++) {
                final BakedQuad q = quads.get(j);
                emitter.fromVanilla(q, MATERIAL_STANDARD, cullFace);
                emitter.emit();
            }
        }
    }
}