package Leclair.graphics.shader;

import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_success;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_spv;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_include_callbacks;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_optimization_level;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_target_env;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_target_spirv;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_release;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compute_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_env_version_vulkan_1_2;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_fragment_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_optimization_level_performance;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_bytes;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_compilation_status;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_error_message;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_length;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_release;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_spirv_version_1_4;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_target_env_vulkan;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_vertex_shader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.asset.AssetLoader;
import Leclair.graphics.GraphicsInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.ShadercIncludeResolve;
import org.lwjgl.util.shaderc.ShadercIncludeResult;
import org.lwjgl.util.shaderc.ShadercIncludeResultRelease;

public class Shader {

    public boolean compiled = false;
    public String filePath = null;
    public ByteBuffer binaryCode;
    public byte shaderType;

    public static List<Shader> shaders = new ArrayList<Shader>();

    public Shader(String path, byte type) {
        this.filePath = path;
        this.shaderType = type;
        shaders.add(this);
    }

    public void compile() {
        if (this.compiled == true) {
            return;
        } else {
            try {
                this.binaryCode = glslToSpirv(this.filePath, this.shaderType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.compiled = true;
        }
    }

    public boolean isCompiled() {
        return this.compiled;
    }

    public ByteBuffer getBinaryCode() {
        return this.binaryCode;
    }

    public static ByteBuffer glslToSpirv(String classPath, byte shaderType) throws IOException {
        ByteBuffer src = AssetLoader.importAsBinary(classPath, 1024);
        long compiler = shaderc_compiler_initialize();
        long options = shaderc_compile_options_initialize();
        ShadercIncludeResolve resolver;
        ShadercIncludeResultRelease releaser;
        shaderc_compile_options_set_target_env(options, shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_2);
        shaderc_compile_options_set_target_spirv(options, shaderc_spirv_version_1_4);
        shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
        shaderc_compile_options_set_include_callbacks(options, resolver = new ShadercIncludeResolve() {
            public long invoke(long user_data, long requested_source, int type, long requesting_source,
                    long include_depth) {
                ShadercIncludeResult res = ShadercIncludeResult.calloc();
                    String src = classPath.substring(0, classPath.lastIndexOf('/')) + "/"
                            + MemoryUtil.memUTF8(requested_source);
                    res.content(AssetLoader.importAsBinary(src, 1024));
                    res.source_name(MemoryUtil.memUTF8(src));
                    return res.address();
            }
        }, releaser = new ShadercIncludeResultRelease() {
            public void invoke(long user_data, long include_result) {
                ShadercIncludeResult result = ShadercIncludeResult.create(include_result);
                MemoryUtil.memFree(result.source_name());
                result.free();
            }
        }, 0L);
        long res;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            res = shaderc_compile_into_spv(compiler, src, bbTypeToShadercKind(shaderType), stack.UTF8(classPath),
                    stack.UTF8("main"), options);
            if (res == 0L)
                throw new AssertionError("Internal error during compilation!");

            if (shaderc_result_get_compilation_status(res) != shaderc_compilation_status_success) {
                throw new AssertionError("Shader compilation failed: " + shaderc_result_get_error_message(res));
            }
            int size = (int) shaderc_result_get_length(res);
            ByteBuffer resultBytes = BufferUtils.createByteBuffer(size);
            resultBytes.put(shaderc_result_get_bytes(res));
            resultBytes.flip();
            shaderc_result_release(res);
            shaderc_compiler_release(compiler);
            releaser.free();
            resolver.free();
            return resultBytes;
        }
    }

    private static int bbTypeToShadercKind(byte shaderType) {
        if (shaderType == GraphicsInfo.FRAGMENT_SHADER) {
            return shaderc_fragment_shader;
        } else if (shaderType == GraphicsInfo.VERTEX_SHADER) {
            return shaderc_vertex_shader;
        } else if (shaderType == GraphicsInfo.COMPUTE_SHADER) {
            return shaderc_compute_shader;
        } else {
            throw new RuntimeException("Unknown Type");
        }
    }

}
