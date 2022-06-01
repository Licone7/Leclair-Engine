package Leclair.window.win32;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.MSG;
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

    static long hwnd;
    static MSG msg = MSG.malloc();
    POINT point = POINT.malloc();
    static WindowProc windowProc;

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            windowProc = new WindowProc() {
                public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                    switch (uMsg) {
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

                        // break;
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

    @Override
    public void loop() {
        while (User32.PeekMessage(msg, 0, 0, 0, User32.PM_NOREMOVE) == true) {
            if (User32.GetMessage(msg, 0, 0, 0)) {
                User32.TranslateMessage(msg);
                User32.DispatchMessage(msg);
            }
        }
        User32.GetCursorPos(point);
        CursorHandler.cursorXPosition = point.x();
        CursorHandler.cursorYPosition = point.y();
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
        point.free();
        msg.free();
        windowProc.free();
    }

}
