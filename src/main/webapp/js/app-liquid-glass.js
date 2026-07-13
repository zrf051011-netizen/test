(function () {
    'use strict';

    var VERTEX_SHADER = [
        'attribute vec2 a_position;',
        'attribute vec2 a_uv;',
        'varying vec2 v_uv;',
        'void main() {',
        '    v_uv = a_uv;',
        '    gl_Position = vec4(a_position, 0.0, 1.0);',
        '}'
    ].join('\n');

    var FRAGMENT_SHADER = [
        'precision highp float;',
        'uniform sampler2D u_background;',
        'uniform vec2 u_viewport;',
        'uniform vec2 u_image_size;',
        'uniform vec2 u_pointer;',
        'uniform float u_pointer_strength;',
        'uniform float u_time;',
        'varying vec2 v_uv;',

        'vec2 backgroundUv(vec2 screenPixel) {',
        '    float coverScale = max(u_viewport.x / u_image_size.x, u_viewport.y / u_image_size.y);',
        '    vec2 renderedSize = u_image_size * coverScale;',
        '    vec2 crop = (renderedSize - u_viewport) * 0.5;',
        '    return clamp((screenPixel + crop) / renderedSize, vec2(0.001), vec2(0.999));',
        '}',

        'vec3 sampleBackground(vec2 screenPixel) {',
        '    return texture2D(u_background, backgroundUv(screenPixel)).rgb;',
        '}',

        'void main() {',
        '    vec2 screenPixel = v_uv * u_viewport;',
        '    vec2 centered = screenPixel - u_viewport * 0.5;',
        '    vec2 flow = vec2(',
        '        sin(screenPixel.y * 0.008 + screenPixel.x * 0.0027 + u_time * 0.28)',
        '            + 0.48 * sin(screenPixel.y * 0.0036 - screenPixel.x * 0.0062 - u_time * 0.19),',
        '        cos(screenPixel.x * 0.0068 - screenPixel.y * 0.0024 - u_time * 0.24)',
        '            + 0.44 * cos(screenPixel.x * 0.0032 + screenPixel.y * 0.0054 + u_time * 0.17)',
        '    );',
        '    vec2 samplePixel = screenPixel + flow * 3.1;',
        '    samplePixel -= centered * 0.0035;',

        '    vec2 pointerPixel = u_pointer * u_viewport;',
        '    vec2 fromPointer = screenPixel - pointerPixel;',
        '    float pointerDistance = max(length(fromPointer), 0.001);',
        '    vec2 pointerNormal = fromPointer / pointerDistance;',
        '    float pointerRadius = mix(120.0, 190.0, clamp(min(u_viewport.x, u_viewport.y) / 900.0, 0.0, 1.0));',
        '    float lens = exp(-2.4 * pow(pointerDistance / pointerRadius, 2.0)) * u_pointer_strength;',
        '    samplePixel -= fromPointer * lens * 0.008;',
        '    samplePixel += pointerNormal * lens * 4.8;',

        '    vec3 centerColor = sampleBackground(samplePixel);',
        '    vec3 softColor = centerColor * 0.52;',
        '    softColor += sampleBackground(samplePixel + vec2(1.8, 0.0)) * 0.12;',
        '    softColor += sampleBackground(samplePixel - vec2(1.8, 0.0)) * 0.12;',
        '    softColor += sampleBackground(samplePixel + vec2(0.0, 1.8)) * 0.12;',
        '    softColor += sampleBackground(samplePixel - vec2(0.0, 1.8)) * 0.12;',

        '    float luminance = dot(softColor, vec3(0.2126, 0.7152, 0.0722));',
        '    vec3 color = mix(vec3(luminance), softColor, 0.48);',
        '    color = mix(color, vec3(0.69, 0.80, 0.87), 0.68);',
        '    vec2 screenUv = screenPixel / u_viewport;',
        '    float upperPearl = 1.0 - smoothstep(0.0, 0.5, distance(screenUv, vec2(0.78, 0.04)));',
        '    float lowerBlue = 1.0 - smoothstep(0.0, 0.52, distance(screenUv, vec2(0.1, 0.94)));',
        '    color += vec3(0.11, 0.15, 0.18) * upperPearl * 0.18;',
        '    color -= vec3(0.025, 0.045, 0.055) * lowerBlue * 0.12;',
        '    float compression = pow(0.5 + 0.5 * sin(flow.x * 2.4 + flow.y * 2.0), 8.0);',
        '    color += vec3(0.48, 0.69, 0.82) * compression * 0.018;',
        '    gl_FragColor = vec4(clamp(color, 0.0, 1.0), 1.0);',
        '}'
    ].join('\n');

    function clamp(value, minimum, maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    function mix(current, target, amount) {
        return current + (target - current) * amount;
    }

    function compileShader(gl, type, source) {
        var shader = gl.createShader(type);
        gl.shaderSource(shader, source);
        gl.compileShader(shader);
        if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
            var message = gl.getShaderInfoLog(shader) || 'Unknown shader compilation error';
            gl.deleteShader(shader);
            throw new Error(message);
        }
        return shader;
    }

    function createProgram(gl) {
        var vertexShader = compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER);
        var fragmentShader = compileShader(gl, gl.FRAGMENT_SHADER, FRAGMENT_SHADER);
        var program = gl.createProgram();
        gl.attachShader(program, vertexShader);
        gl.attachShader(program, fragmentShader);
        gl.linkProgram(program);
        gl.deleteShader(vertexShader);
        gl.deleteShader(fragmentShader);
        if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
            var message = gl.getProgramInfoLog(program) || 'Unknown program link error';
            gl.deleteProgram(program);
            throw new Error(message);
        }
        return program;
    }

    function AppLiquidScene(canvas) {
        this.canvas = canvas;
        this.body = document.body;
        this.backgroundUrl = canvas.getAttribute('data-liquid-background');
        this.gl = null;
        this.program = null;
        this.buffer = null;
        this.texture = null;
        this.image = null;
        this.locations = null;
        this.raf = 0;
        this.lastFrameTime = 0;
        this.pointer = { x: 0.72, y: 0.18 };
        this.pointerTarget = { x: 0.72, y: 0.18 };
        this.pointerStrength = 0;
        this.pointerStrengthTarget = 0;
        this.visible = true;
        this.destroyed = false;
        this.reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.forcedColors = window.matchMedia && window.matchMedia('(forced-colors: active)').matches;
        this.reducedTransparency = window.matchMedia && window.matchMedia('(prefers-reduced-transparency: reduce)').matches;
        this.handlePointerMove = this.handlePointerMove.bind(this);
        this.handlePointerLeave = this.handlePointerLeave.bind(this);
        this.handleResize = this.handleResize.bind(this);
        this.handleVisibility = this.handleVisibility.bind(this);
        this.frame = this.frame.bind(this);
    }

    AppLiquidScene.prototype.init = function () {
        var self = this;
        if (!this.backgroundUrl || this.forcedColors || this.reducedTransparency) {
            return;
        }
        try {
            this.gl = this.canvas.getContext('webgl', {
                alpha: false,
                antialias: false,
                depth: false,
                stencil: false,
                premultipliedAlpha: false,
                preserveDrawingBuffer: true,
                powerPreference: 'high-performance'
            });
            if (!this.gl) {
                return;
            }
            this.program = createProgram(this.gl);
            this.setupGeometry();
            this.cacheLocations();
        } catch (error) {
            this.fail(error);
            return;
        }

        this.image = new Image();
        this.image.decoding = 'async';
        this.image.onload = function () {
            if (self.destroyed) {
                return;
            }
            try {
                self.setupTexture();
                self.bindEvents();
                self.resize();
                self.body.classList.add('is-app-liquid-ready');
                self.body.setAttribute('data-app-liquid-engine', 'webgl');
                self.requestDraw();
            } catch (error) {
                self.fail(error);
            }
        };
        this.image.onerror = function () {
            self.fail(new Error('Application liquid background image could not be loaded'));
        };
        this.image.src = this.backgroundUrl;
    };

    AppLiquidScene.prototype.setupGeometry = function () {
        var gl = this.gl;
        var vertices = new Float32Array([
            -1, -1, 0, 1,
             1, -1, 1, 1,
            -1,  1, 0, 0,
             1,  1, 1, 0
        ]);
        this.buffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, this.buffer);
        gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);
    };

    AppLiquidScene.prototype.cacheLocations = function () {
        var gl = this.gl;
        this.locations = {
            position: gl.getAttribLocation(this.program, 'a_position'),
            uv: gl.getAttribLocation(this.program, 'a_uv'),
            background: gl.getUniformLocation(this.program, 'u_background'),
            viewport: gl.getUniformLocation(this.program, 'u_viewport'),
            imageSize: gl.getUniformLocation(this.program, 'u_image_size'),
            pointer: gl.getUniformLocation(this.program, 'u_pointer'),
            pointerStrength: gl.getUniformLocation(this.program, 'u_pointer_strength'),
            time: gl.getUniformLocation(this.program, 'u_time')
        };
    };

    AppLiquidScene.prototype.setupTexture = function () {
        var gl = this.gl;
        this.texture = gl.createTexture();
        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, this.texture);
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, false);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.image);
    };

    AppLiquidScene.prototype.bindEvents = function () {
        var self = this;
        document.addEventListener('pointermove', this.handlePointerMove, { passive: true });
        document.addEventListener('pointerleave', this.handlePointerLeave, { passive: true });
        window.addEventListener('resize', this.handleResize, { passive: true });
        document.addEventListener('visibilitychange', this.handleVisibility);
        this.canvas.addEventListener('webglcontextlost', function (event) {
            event.preventDefault();
            self.body.classList.remove('is-app-liquid-ready');
            self.body.removeAttribute('data-app-liquid-engine');
            if (self.raf) {
                cancelAnimationFrame(self.raf);
                self.raf = 0;
            }
        });
    };

    AppLiquidScene.prototype.handlePointerMove = function (event) {
        if (this.reducedMotion || event.pointerType === 'touch') {
            return;
        }
        this.pointerTarget.x = clamp(event.clientX / Math.max(window.innerWidth, 1), 0, 1);
        this.pointerTarget.y = clamp(event.clientY / Math.max(window.innerHeight, 1), 0, 1);
        var target = event.target && event.target.closest
            ? event.target.closest('.stat-card, .dashboard-panel, .student-hero, .notice-panel, .modal, .topbar, .app-sidebar')
            : null;
        this.pointerStrengthTarget = target ? 1 : 0;
        this.requestDraw();
    };

    AppLiquidScene.prototype.handlePointerLeave = function () {
        this.pointerStrengthTarget = 0;
        this.requestDraw();
    };

    AppLiquidScene.prototype.handleResize = function () {
        this.resize();
        this.requestDraw();
    };

    AppLiquidScene.prototype.handleVisibility = function () {
        this.visible = !document.hidden;
        if (this.visible) {
            this.requestDraw();
        } else if (this.raf) {
            cancelAnimationFrame(this.raf);
            this.raf = 0;
        }
    };

    AppLiquidScene.prototype.resize = function () {
        if (!this.gl) {
            return;
        }
        var mobile = window.innerWidth <= 760;
        var pixelRatio = Math.min(window.devicePixelRatio || 1, mobile ? 1 : 1.25);
        var width = Math.max(1, Math.round(window.innerWidth * pixelRatio));
        var height = Math.max(1, Math.round(window.innerHeight * pixelRatio));
        if (this.canvas.width !== width || this.canvas.height !== height) {
            this.canvas.width = width;
            this.canvas.height = height;
        }
        this.gl.viewport(0, 0, width, height);
    };

    AppLiquidScene.prototype.requestDraw = function () {
        if (this.destroyed || !this.visible || !this.gl || !this.texture || this.raf) {
            return;
        }
        this.raf = requestAnimationFrame(this.frame);
    };

    AppLiquidScene.prototype.frame = function (now) {
        this.raf = 0;
        if (this.destroyed || !this.visible || document.hidden) {
            return;
        }
        var active = this.pointerStrength > 0.01 || this.pointerStrengthTarget > 0.01;
        var minimumInterval = active ? 22 : 66;
        if (!this.reducedMotion && this.lastFrameTime && now - this.lastFrameTime < minimumInterval) {
            this.raf = requestAnimationFrame(this.frame);
            return;
        }
        var elapsed = this.lastFrameTime ? Math.min((now - this.lastFrameTime) / 16.667, 4) : 1;
        this.lastFrameTime = now;
        var amount = this.reducedMotion ? 1 : 1 - Math.pow(0.84, elapsed);
        this.pointer.x = mix(this.pointer.x, this.pointerTarget.x, amount);
        this.pointer.y = mix(this.pointer.y, this.pointerTarget.y, amount);
        this.pointerStrength = mix(this.pointerStrength, this.pointerStrengthTarget, amount);
        this.draw(this.reducedMotion ? 0 : now * 0.001);
        if (!this.reducedMotion) {
            this.raf = requestAnimationFrame(this.frame);
        }
    };

    AppLiquidScene.prototype.draw = function (time) {
        var gl = this.gl;
        gl.useProgram(this.program);
        gl.bindBuffer(gl.ARRAY_BUFFER, this.buffer);
        gl.enableVertexAttribArray(this.locations.position);
        gl.vertexAttribPointer(this.locations.position, 2, gl.FLOAT, false, 16, 0);
        gl.enableVertexAttribArray(this.locations.uv);
        gl.vertexAttribPointer(this.locations.uv, 2, gl.FLOAT, false, 16, 8);
        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, this.texture);
        gl.uniform1i(this.locations.background, 0);
        gl.uniform2f(this.locations.viewport, window.innerWidth, window.innerHeight);
        gl.uniform2f(this.locations.imageSize, this.image.naturalWidth, this.image.naturalHeight);
        gl.uniform2f(this.locations.pointer, this.pointer.x, this.pointer.y);
        gl.uniform1f(this.locations.pointerStrength, this.pointerStrength);
        gl.uniform1f(this.locations.time, time);
        gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    };

    AppLiquidScene.prototype.fail = function (error) {
        this.body.classList.remove('is-app-liquid-ready');
        this.body.removeAttribute('data-app-liquid-engine');
        if (window.console && console.warn) {
            console.warn('[app-liquid-glass] WebGL enhancement disabled:', error && error.message ? error.message : error);
        }
    };

    function init() {
        var canvas = document.querySelector('[data-app-liquid-scene]');
        if (!canvas) {
            return;
        }
        var scene = new AppLiquidScene(canvas);
        scene.init();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
}());
