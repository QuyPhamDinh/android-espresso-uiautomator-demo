package com.wdiodemoapp.framework.core

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import org.junit.rules.ExternalResource

/**
 * Deterministic in-process HTTP server serving an ES5-compatible WebdriverIO DocSearch fixture
 * so WebView instrumentation tests never depend on external network availability or live site DOMs.
 */
class LocalWebServerRule : ExternalResource() {

    private var serverSocket: ServerSocket? = null
    private var acceptThread: Thread? = null

    val baseUrl: String
        get() {
            val socket = checkNotNull(serverSocket) { "LocalWebServerRule has not been started" }
            return "http://127.0.0.1:${socket.localPort}/"
        }

    override fun before() {
        val socket = ServerSocket(0, 16, InetAddress.getByName("127.0.0.1"))
        serverSocket = socket
        acceptThread = Thread({
            while (!socket.isClosed) {
                val client = runCatching { socket.accept() }.getOrNull() ?: break
                Thread({ handleClient(client) }, "LocalWebServer-Client").apply {
                    isDaemon = true
                    start()
                }
            }
        }, "LocalWebServer-Accept").apply {
            isDaemon = true
            start()
        }
    }

    override fun after() {
        runCatching { serverSocket?.close() }
        serverSocket = null
        acceptThread = null
    }

    private fun handleClient(client: Socket) {
        client.use { socket ->
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
            val requestLine = reader.readLine() ?: return
            while (true) {
                val header = reader.readLine() ?: break
                if (header.isEmpty()) break
            }

            val rawTarget = requestLine.split(' ').getOrNull(1).orEmpty()
            val path = rawTarget.substringBefore('?')

            val (status, contentType, body) = when (path) {
                "/", "/index.html" -> Triple("200 OK", "text/html; charset=utf-8", HOME_HTML)
                "/docs/api/browser/url" -> Triple(
                    "200 OK",
                    "text/html; charset=utf-8",
                    docPageHtml("browser.url", "Navigates the current browser session to the specified URL."),
                )
                "/docs/ocr-testing/ocr-testing" -> Triple(
                    "200 OK",
                    "text/html; charset=utf-8",
                    docPageHtml("OCR Testing", "Verify visual text elements reliably across mobile and web."),
                )
                "/favicon.ico" -> Triple("204 No Content", "text/plain; charset=utf-8", "")
                else -> Triple("404 Not Found", "text/plain; charset=utf-8", "Not Found: $path")
            }

            val bodyBytes = body.toByteArray(StandardCharsets.UTF_8)
            val headers = buildString {
                append("HTTP/1.1 ").append(status).append("\r\n")
                append("Content-Type: ").append(contentType).append("\r\n")
                append("Content-Length: ").append(bodyBytes.size).append("\r\n")
                append("Cache-Control: no-store\r\n")
                append("Connection: close\r\n\r\n")
            }
            val out = socket.getOutputStream()
            out.write(headers.toByteArray(StandardCharsets.US_ASCII))
            if (bodyBytes.isNotEmpty()) {
                out.write(bodyBytes)
            }
            out.flush()
        }
    }

    private companion object {
        fun docPageHtml(title: String, summary: String): String =
            """
            <!doctype html>
            <html lang="en">
            <head><meta charset="utf-8"><title>$title | WebdriverIO</title></head>
            <body>
              <header><h1>$title</h1></header>
              <main><p>$summary</p></main>
            </body>
            </html>
            """.trimIndent()

        // ES5-only script so Chromium 83 (API 30) and modern WebViews parse it identically.
        val HOME_HTML =
            """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>WebdriverIO</title>
              <style>
                body { font-family: sans-serif; margin: 16px; }
                .DocSearch { padding: 10px 14px; font-size: 16px; }
                .DocSearch-Input { width: 100%; padding: 10px; font-size: 16px; margin-top: 12px; }
                .DocSearch-Hits { list-style: none; padding: 0; margin-top: 12px; }
                .DocSearch-Hit { padding: 8px 0; border-bottom: 1px solid #ddd; }
              </style>
            </head>
            <body>
              <header><h1>WebdriverIO</h1></header>
              <button type="button" class="DocSearch">Search docs</button>
              <div id="docsearch-container"></div>
              <script>
                (function () {
                  var docs = [
                    { title: 'url', href: '/docs/api/browser/url' },
                    { title: 'OCR Testing', href: '/docs/ocr-testing/ocr-testing' }
                  ];
                  var openBtn = document.querySelector('.DocSearch');
                  var container = document.getElementById('docsearch-container');

                  function renderHits(listEl, query) {
                    var q = (query || '').toLowerCase();
                    listEl.innerHTML = '';
                    var hitIndex = 0;
                    for (var i = 0; i < docs.length; i++) {
                      var item = docs[i];
                      if (q === '' || item.title.toLowerCase().indexOf(q) !== -1) {
                        var li = document.createElement('li');
                        li.className = 'DocSearch-Hit';
                        li.id = 'docsearch-hits0-item-' + hitIndex;
                        var link = document.createElement('a');
                        link.href = item.href;
                        link.textContent = item.title;
                        li.appendChild(link);
                        listEl.appendChild(li);
                        hitIndex++;
                      }
                    }
                  }

                  function openSearchModal() {
                    if (document.querySelector('.DocSearch-Input')) {
                      return;
                    }
                    var input = document.createElement('input');
                    input.type = 'search';
                    input.className = 'DocSearch-Input';
                    input.placeholder = 'Search docs';

                    var list = document.createElement('ul');
                    list.className = 'DocSearch-Hits';

                    input.addEventListener('input', function () {
                      renderHits(list, input.value);
                    });
                    input.addEventListener('change', function () {
                      renderHits(list, input.value);
                    });

                    container.appendChild(input);
                    container.appendChild(list);
                    renderHits(list, '');
                  }

                  openBtn.addEventListener('click', openSearchModal);
                })();
              </script>
            </body>
            </html>
            """.trimIndent()
    }
}
