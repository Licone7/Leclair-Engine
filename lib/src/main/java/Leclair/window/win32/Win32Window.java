package Leclair.window.win32;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.MSG;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowProc;
import org.lwjgl.system.windows.WindowsLibrary;

import Leclair.application.ApplicationStructure;
import Leclair.window.Window;

/**
 * @author Brett Burnett
 */
public class Win32Window implements Window {

    long hwnd;
    MSG msg;

    @Override
    public void init() {
        WindowProc windowProc = new WindowProc() {
            public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                return User32.DefWindowProc(hwnd, uMsg, wParam, lParam);
            }
        };
        String className = "AWTAPPWNDCLASS";
        WNDCLASSEX in = WNDCLASSEX.calloc();
        in.cbSize(WNDCLASSEX.SIZEOF);
        in.lpfnWndProc(windowProc);
        in.hInstance(WindowsLibrary.HINSTANCE);
        ByteBuffer classNameBuffer = MemoryUtil.memUTF16(className);
        in.lpszClassName(classNameBuffer);
        User32.RegisterClassEx(in);
        hwnd = User32.CreateWindowEx(User32.WS_EX_APPWINDOW, className, "Test", User32.WS_OVERLAPPEDWINDOW, User32.CW_USEDEFAULT,
                User32.CW_USEDEFAULT, 800, 600, 0, 0,
                WindowsLibrary.HINSTANCE, windowProc.address());
        if (hwnd == 0) {
            throw new IllegalStateException("Cannot create a window");
        }
        MemoryUtil.memFree(classNameBuffer);
       // in.free();
        User32.ShowWindow(hwnd, User32.SW_SHOW);
        msg = MSG.calloc();
    }

    @Override
    public void loop() {
        if (User32.GetMessage(msg, hwnd, 0, 0)) {
            User32.TranslateMessage(msg);
            User32.DispatchMessage(msg);
        } else {
            ApplicationStructure.Stop();
        }

    }

    @Override
    public void show() {

    }

    @Override
    public long getWHandle() {
        return hwnd;
    }

    @Override
    public void destroy() {
        
    }

}