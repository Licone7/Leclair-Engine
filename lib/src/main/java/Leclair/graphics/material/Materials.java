package Leclair.graphics.material;

public interface Materials {
    /**
     * Handle to an unlit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte UNLIT_MATERIAL = 20;

    /**
     * Handle to a lit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte LIT_MATERIAL = 21;

    /**
     * Handle to a PBR lit material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte PBR_LIT_MATERIAL = 22;

     /**
     * Handle to a terrain material <br>
     * <br>
     * Used to specify the material type when creating a new Material();
     */
    public static final byte TERRAIN_MATERIAL = 23;
}