package com.bergerkiller.bukkit.common.server;

import java.util.Map;

import com.bergerkiller.generated.red.mohist.bukkit.nms.utils.RemapUtilsHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.ClassNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * Mohist is a PaperSpigot + Forge implementation
 */
public class MohistServer extends PaperSpigotServer implements FieldNameResolver, MethodNameResolver, ClassNameResolver {
    private static ClassDeclaration remapUtilsClassDec = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Mohist server, we expect this Class to exist
        try {
            Class.forName("red.mohist.Mohist");
        } catch (Throwable t) {
            return false;
        }

        // Initialize the RemapUtils Class Declaration
        {
            // Decode the remaputils.txt file
            String templatePath = "com/bergerkiller/templates/red/mohist/bukkit/nms/utils/remaputils.txt";
            ClassLoader classLoader = MohistServer.class.getClassLoader();
            SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath);
            if (sourceDec.classes.length != 1) {
                return false;
            }

            remapUtilsClassDec = sourceDec.classes[0];
        }

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        if (!RemapUtilsHandle.T.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        RemapUtilsHandle.T.forceInitialization();

        return true;
    }

    @Override
    public String getServerName() {
        return "Mohist";
    }

    @Override
    public String resolveClassPath(String path) {
        // Replaces path with proper net.minecraft.server.v1_1_1 path
        path = super.resolveClassPath(path);

        // Ask Mohist what the actual class name is on Forge
        path = RemapUtilsHandle.mapClassName(path);

        return path;
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        return RemapUtilsHandle.inverseMapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        return RemapUtilsHandle.inverseMapFieldName(type, fieldName);
    }

    @Override
    public String resolveClassName(Class<?> clazz) {
        return RemapUtilsHandle.inverseMapClassName(clazz);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "mohist");
    }

    // Used by the RemapUtilsHandle because it is used before the actual template engine is initialized
    public static final ClassDeclarationResolver TEMPLATE_RESOLVER = (classPath, classType) -> {
        if (classPath.equals("red.mohist.bukkit.nms.utils.RemapUtils")) {
            return remapUtilsClassDec;
        }
        return null;
    };
}
