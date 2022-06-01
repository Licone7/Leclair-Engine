package Leclair.window.win32;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.WGL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.MSG;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;
import org.lwjgl.system.windows.POINT;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowProc;
import org.lwjgl.system.windows.WindowsLibrary;

import Leclair.application.ApplicationStructure;
import Leclair.input.key.KeyHandler;
import Leclair.input.mouse.CursorHandler;
import Leclair.input.mouse.MouseButtonHandler;
import Leclair.input.mouse.MouseButtons;
import Leclair.window.Window;
import Leclair.window.WindowInfo;

public class Win32Window implements Window {

    static long hdc;
    static long hglrc;
    static long hwnd;
    static MSG msg = MSG.malloc();
    static WindowProc windowProc;

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            windowProc = new WindowProc() {
                public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                    switch (uMsg) {
                        case User32.WM_CREATE:
                            hdc = User32.GetDC(hwnd);
                            PIXELFORMATDESCRIPTOR ppfd = PIXELFORMATDESCRIPTOR.calloc(stack);
                            ppfd.nSize((short) PIXELFORMATDESCRIPTOR.SIZEOF);
                            ppfd.nVersion((short) 1);
                            ppfd.dwFlags(GDI32.PFD_DRAW_TO_WINDOW | GDI32.PFD_SUPPORT_OPENGL |
                                    GDI32.PFD_DOUBLEBUFFER);
                            ppfd.dwLayerMask(GDI32.PFD_MAIN_PLANE);
                            int pixelformat = GDI32.ChoosePixelFormat(hdc, ppfd);
                            if ((pixelformat = GDI32.ChoosePixelFormat(hdc, ppfd)) == 0) {

                            }
                            if (GDI32.SetPixelFormat(hdc, pixelformat, ppfd) == false) {

                            }
                            hglrc = WGL.wglCreateContext(hdc);
                            WGL.wglMakeCurrent(hdc, hglrc);
                            break;
                        case User32.WM_CLOSE:
                            ApplicationStructure.stop();
                            break;
                        case User32.WM_KEYDOWN:
                            KeyHandler.KEY_PRESS = true;
                            KeyHandler.CHAR = (char) wParam;
                            break;
                        case User32.WM_KEYUP:
                            KeyHandler.KEY_PRESS = false;
                            break;
                        case User32.WM_LBUTTONDOWN:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = true;
                            MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_LEFT;
                            break;
                        case User32.WM_LBUTTONUP:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = false;
                            break;
                        case User32.WM_RBUTTONDOWN:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = true;
                            MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_RIGHT;
                            break;
                        case User32.WM_RBUTTONUP:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = false;
                            break;
                        case User32.WM_MBUTTONDOWN:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = true;
                            MouseButtonHandler.MOUSE_BUTTON = MouseButtons.MOUSE_BUTTON_MIDDLE;
                            break;
                        case User32.WM_MBUTTONUP:
                            MouseButtonHandler.MOUSE_BUTTON_PRESS = false;
                            break;
                        // case User32.WM_MOUSEMOVE:
                
                        //     break;
                        case User32.WM_PAINT:
                            GL.createCapabilities();
                            break;
                    }
                    return User32.DefWindowProc(hwnd, uMsg, wParam, lParam);
                }
            };
            String className = "AWTAPPWNDCLASS";
            WNDCLASSEX in = WNDCLASSEX.calloc(stack);
            in.cbSize(WNDCLASSEX.SIZEOF);
            in.lpfnWndProc(windowProc);
            in.hInstance(WindowsLibrary.HINSTANCE);
            ByteBuffer classNameBuffer = stack.UTF16(className);
            in.lpszClassName(classNameBuffer);
            User32.RegisterClassEx(in);
            hwnd = User32.CreateWindowEx(User32.WS_EX_APPWINDOW, className, WindowInfo.getTitle(),
                    User32.WS_OVERLAPPEDWINDOW,
                    User32.CW_USEDEFAULT,
                    User32.CW_USEDEFAULT, WindowInfo.getWidth(), WindowInfo.getHeight(), 0, 0,
                    WindowsLibrary.HINSTANCE, windowProc.address());
            if (hwnd == 0) {
                throw new IllegalStateException("Cannot create a window");
            }
        }
    }
    POINT point = POINT.calloc();

    @Override
    public void loop() {
        // while (ApplicationStructure.RUNNING == true) {
        while (User32.PeekMessage(msg, 0, 0, 0, User32.PM_NOREMOVE) == true) {
            if (User32.GetMessage(msg, 0, 0, 0)) {
                User32.TranslateMessage(msg);
                User32.DispatchMessage(msg);
            }
        }
                        User32.GetCursorPos(point);
                      //  System.out.println(point.x() + " " + point.y());
                           CursorHandler.cursorXPosition = point.x();
                            CursorHandler.cursorYPosition = point.y();
        // GL11.glClearColor(0, 1, 0, 0);
        // GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GDI32.SwapBuffers(hdc);
        //System.out.println(MouseButtonHandler.MOUSE_BUTTON_PRESS);
        // }
    }

    @Override
    public void show() {
        User32.ShowWindow(hwnd, User32.SW_SHOW);
    }

    @Override
    public long getWHandle() {
        return hwnd;
    }

    @Override
    public void destroy() {
        User32.DestroyWindow(hwnd);
        msg.free();
        windowProc.free();
    }

}
