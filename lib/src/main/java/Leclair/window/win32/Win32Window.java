package Leclair.window.win32;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.MSG;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowProc;
import org.lwjgl.system.windows.WindowsLibrary;

import Leclair.application.ApplicationInfo;
import Leclair.application.ApplicationStructure;
import Leclair.input.InputData;
import Leclair.window.Window;
import Leclair.window.WindowInfo;

/**
 * All applications running on the Windows OS will use this class to create
 * windows via the Win32 API.
 * 
 * @since v1
 * @author Brett Burnett
 */
public class Win32Window implements Window {

    long hwnd;
    MSG msg = MSG.malloc();
    WindowProc windowProc;

    @Override
    public void init() {
        windowProc = new WindowProc() {
            public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                switch (uMsg) {
                    case User32.WM_CLOSE:
                        User32.DestroyWindow(hwnd);
                        ApplicationStructure.stop();
                        break;
                    case User32.WM_KEYDOWN:
                        InputData.KEY_DOWN = true;
                        InputData.CHAR = (char) wParam;
                        break;
                }
                return User32.DefWindowProc(hwnd, uMsg, wParam, lParam);
            }
        };
        try (MemoryStack stack = MemoryStack.stackPush()) {
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
        // boolean messageReceived = User32.GetMessage(msg, hwnd, 0, 0);
        // if (messageReceived) {
        // User32.TranslateMessage(msg);
        // User32.DispatchMessage(msg);
        // }
        // System.out.println(messageReceived);
        // if (User32.GetMessage(msg, hwnd, 0, 0)) {
        // User32.TranslateMessage(msg);
        // User32.DispatchMessage(msg);
        // } else {
        // ApplicationStructure.stop();
        // }
        User32.PeekMessage(msg, hwnd, 0, 0, User32.PM_REMOVE);
        if (msg.message() != User32.WM_PAINT) {
            System.out.println(msg.message());
        }
        switch (msg.message()) {
            case User32.WM_PAINT:

                break;
            case User32.WM_KEYDOWN:
                InputData.KEY_DOWN = true;
                System.out.println("TET");
               // InputData.CHAR = (char) wParam;
                break;
            case User32.WM_CLOSE:
                System.exit(0);
                break;

            default:
                break;
        }
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
        msg.free();
        windowProc.free();
    }

}