"""
Zenith '26 Fest System Server Launcher
Starts Uvicorn with dual-stack IPv4/IPv6 support on Windows.
Binds to localhost, 127.0.0.1, [::1], and local network IP on port 8001.
Includes mobile APK download endpoints for direct phone installation.
"""
import socket
import sys
from pathlib import Path
import uvicorn
from fastapi.responses import FileResponse, HTMLResponse

# Add project root to sys.path
sys.path.insert(0, str(Path(__file__).resolve().parent))
from backend.main import app

BASE_DIR = Path(__file__).resolve().parent
APK_PATH = BASE_DIR / "app-debug.apk"

@app.api_route("/app-debug.apk", methods=["GET", "HEAD"], include_in_schema=False)
@app.api_route("/download/zenith26.apk", methods=["GET", "HEAD"], include_in_schema=False)
def download_apk():
    if not APK_PATH.exists():
        return HTMLResponse("<h1>APK not found. Please build the APK first.</h1>", status_code=404)
    return FileResponse(
        APK_PATH,
        media_type="application/vnd.android.package-archive",
        filename="zenith26-fest.apk"
    )

@app.api_route("/qr.png", methods=["GET", "HEAD"], include_in_schema=False)
def serve_qr():
    return FileResponse(BASE_DIR / "qr.png", media_type="image/png")

@app.api_route("/download", methods=["GET", "HEAD"], include_in_schema=False)
def download_page():
    return HTMLResponse("""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Download Zenith '26 Mobile App</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 24px; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; box-sizing: border-box; text-align: center; }
        .card { background: #1e293b; border-radius: 16px; padding: 32px 24px; max-width: 420px; width: 100%; border: 1px solid #334155; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
        .badge { display: inline-block; background: #065f46; color: #34d399; font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 9999px; margin-bottom: 12px; }
        h1 { color: #38bdf8; font-size: 24px; margin-bottom: 4px; }
        p { color: #94a3b8; font-size: 14px; margin-bottom: 20px; line-height: 1.5; }
        .qr-box { background: white; padding: 12px; border-radius: 12px; display: inline-block; margin-bottom: 20px; box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
        .qr-box img { display: block; width: 180px; height: 180px; }
        .btn { display: inline-block; background: #2563eb; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 12px; font-weight: 600; font-size: 16px; width: 100%; box-sizing: border-box; transition: background 0.2s; box-shadow: 0 4px 14px rgba(37,99,235,0.4); }
        .btn:hover { background: #1d4ed8; }
        .instructions { text-align: left; background: #0f172a; border-radius: 12px; padding: 16px; margin-top: 24px; border: 1px solid #334155; }
        .instructions h3 { color: #e2e8f0; font-size: 14px; margin: 0 0 10px 0; }
        .instructions ol { margin: 0; padding-left: 20px; color: #94a3b8; font-size: 13px; line-height: 1.6; }
    </style>
</head>
<body>
    <div class="card">
        <span class="badge">&#10003; Safe &amp; Verified Local Build</span>
        <h1>Zenith '26 Fest</h1>
        <p>Native Android App for Nothing Phone (2a)</p>
        <div class="qr-box">
            <img src="/qr.png" alt="Scan QR Code to Download" />
        </div>
        <a href="/download/zenith26.apk" class="btn">&#11015;&#65039; Download APK (15.6 MB)</a>
        <div class="instructions">
            <h3>Installation Steps:</h3>
            <ol>
                <li>Tap <b>Download APK</b> above.</li>
                <li>When Chrome displays <i>"File might be harmful"</i> (standard Android message for direct APKs), tap <b>Download anyway</b>.</li>
                <li>Tap <b>Open</b> once downloaded.</li>
                <li>If prompted <i>"Allow from this source"</i>, tap <b>Settings</b> and toggle allow.</li>
                <li>Tap <b>Install</b> to complete!</li>
            </ol>
        </div>
    </div>
</body>
</html>""")

def start_server(port: int = 8001):
    print(f"Starting Zenith '26 Server on port {port} (Dual-Stack IPv4 & IPv6)...")
    
    # Create dual-stack socket for Windows compatibility
    sock = socket.socket(socket.AF_INET6, socket.SOCK_STREAM)
    try:
        sock.setsockopt(socket.IPPROTO_IPV6, socket.IPV6_V6ONLY, 0)
    except Exception:
        pass
    
    sock.bind(("", port))
    sock.listen(128)
    
    print(f" -> Student Portal:       http://localhost:{port}/")
    print(f" -> Admin Dashboard:      http://localhost:{port}/admin.html")
    print(f" -> Download Page:        http://localhost:{port}/download")
    print(f" -> APK Direct Download:  http://localhost:{port}/download/zenith26.apk")
    print(f" -> Interactive API Docs: http://localhost:{port}/docs")
    print(f" -> Android Emulator:     http://10.0.2.2:{port}/")
    print("\nPress Ctrl+C to stop the server.\n")

    config = uvicorn.Config(app=app, log_level="info")
    server = uvicorn.Server(config)
    server.run(sockets=[sock])

if __name__ == "__main__":
    port_arg = int(sys.argv[1]) if len(sys.argv) > 1 else 8001
    start_server(port_arg)
