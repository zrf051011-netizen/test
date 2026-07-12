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
        'uniform vec2 u_card_origin;',
        'uniform vec2 u_card_size;',
        'uniform vec2 u_pointer;',
        'uniform float u_radius;',
        'uniform float u_edge_width;',
        'uniform float u_refraction;',
        'uniform float u_dispersion;',
        'uniform float u_blur;',
        'uniform float u_energy;',
        'uniform float u_time;',
        'uniform float u_variant;',
        'uniform vec2 u_pointer_velocity;',
        'uniform float u_hover;',
        'uniform float u_impulse;',
        'varying vec2 v_uv;',

        'float roundedRectSdf(vec2 p, vec2 halfSize, float radius) {',
        '    vec2 q = abs(p) - halfSize + vec2(radius);',
        '    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;',
        '}',

        'vec2 backgroundUv(vec2 screenPixel) {',
        '    float coverScale = max(u_viewport.x / u_image_size.x, u_viewport.y / u_image_size.y);',
        '    vec2 renderedSize = u_image_size * coverScale;',
        '    vec2 crop = (renderedSize - u_viewport) * 0.5;',
        '    return clamp((screenPixel + crop) / renderedSize, vec2(0.001), vec2(0.999));',
        '}',

        'vec3 sampleBackground(vec2 screenPixel) {',
        '    return texture2D(u_background, backgroundUv(screenPixel)).rgb;',
        '}',

        'vec3 coolCampusGrade(vec3 color, vec2 screenPixel) {',
        '    vec3 sourceColor = color;',
        '    vec2 screenUv = screenPixel / u_viewport;',
        '    float shade = mix(0.62, 0.53, clamp(screenUv.x, 0.0, 1.0));',
        '    shade += (1.0 - screenUv.y) * 0.03;',
        '    color = mix(color, vec3(0.024, 0.088, 0.145), clamp(shade, 0.0, 0.68));',
        '    float topGlow = 1.0 - smoothstep(0.0, 0.38, distance(screenUv, vec2(0.84, 0.12)));',
        '    float lowGlow = 1.0 - smoothstep(0.0, 0.42, distance(screenUv, vec2(0.12, 0.88)));',
        '    color += vec3(0.055, 0.082, 0.105) * topGlow * 0.16;',
        '    color += vec3(0.012, 0.033, 0.055) * lowGlow * 0.11;',
        '    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));',
        '    color = mix(vec3(luminance), color, 0.82);',
        '    vec3 lightGlass = mix(color, vec3(0.69, 0.80, 0.87), 0.17);',
        '    vec3 darkGlass = mix(sourceColor, vec3(0.012, 0.071, 0.125), 0.66);',
        '    float darkLuminance = dot(darkGlass, vec3(0.2126, 0.7152, 0.0722));',
        '    darkGlass = mix(vec3(darkLuminance), darkGlass, 0.78);',
        '    darkGlass += vec3(0.025, 0.070, 0.105) * (topGlow * 0.22 + lowGlow * 0.12);',
        '    return mix(lightGlass, darkGlass, u_variant);',
        '}',

        'void main() {',
        '    vec2 halfSize = max(u_card_size * 0.5 - vec2(1.0), vec2(1.0));',
        '    vec2 localPixel = v_uv * u_card_size;',
        '    vec2 p = localPixel - u_card_size * 0.5;',
        '    float distanceToEdge = roundedRectSdf(p, halfSize, u_radius);',
        '    float insideDepth = max(-distanceToEdge, 0.0);',
        '    float rim = 1.0 - smoothstep(0.0, u_edge_width, insideDepth);',

        '    float normalStep = 1.0;',
        '    float dx = roundedRectSdf(p + vec2(normalStep, 0.0), halfSize, u_radius)',
        '             - roundedRectSdf(p - vec2(normalStep, 0.0), halfSize, u_radius);',
        '    float dy = roundedRectSdf(p + vec2(0.0, normalStep), halfSize, u_radius)',
        '             - roundedRectSdf(p - vec2(0.0, normalStep), halfSize, u_radius);',
        '    vec2 edgeNormal = vec2(dx, dy);',
        '    float normalLength = length(edgeNormal);',
        '    edgeNormal = normalLength > 0.0001 ? edgeNormal / normalLength : vec2(0.0, -1.0);',

        '    vec2 cardCenter = u_card_origin + u_card_size * 0.5;',
        '    float magnification = 0.007 + u_energy * 0.002;',
        '    vec2 samplePixel = cardCenter + p * (1.0 - magnification);',
        '    float wave = sin(p.y * 0.031 + sin(p.x * 0.017) * 1.7 + u_time * 1.25);',
        '    float wave2 = cos(p.x * 0.024 - p.y * 0.011 - u_time * 0.82);',
        '    float liquidWave = (wave * 0.58 + wave2 * 0.42) * (0.65 + u_energy * 0.55);',
        '    vec2 bodyField = vec2(',
        '        sin(p.y * 0.017 + p.x * 0.006 + u_time * 0.48)',
        '            + 0.52 * sin(p.y * 0.009 - p.x * 0.014 - u_time * 0.31),',
        '        cos(p.x * 0.015 - p.y * 0.005 - u_time * 0.4)',
        '            + 0.48 * cos(p.x * 0.008 + p.y * 0.012 + u_time * 0.27)',
        '    );',
        '    float bodyMask = smoothstep(2.0, u_edge_width * 1.7, insideDepth);',
        '    float bodyStrength = mix(1.9, 1.45, u_variant);',
        '    samplePixel += bodyField * bodyStrength * bodyMask;',
        '    samplePixel -= edgeNormal * (u_refraction * rim + liquidWave * rim);',

        '    vec2 pointerPixel = (u_pointer - vec2(0.5)) * u_card_size;',
        '    vec2 fromPointer = p - pointerPixel;',
        '    float pointerDistance = max(length(fromPointer), 0.001);',
        '    vec2 pointerNormal = fromPointer / pointerDistance;',
        '    float lensRadius = mix(112.0, 158.0, clamp(min(u_card_size.x, u_card_size.y) / 720.0, 0.0, 1.0));',
        '    float lensFalloff = exp(-2.35 * pow(pointerDistance / lensRadius, 2.0)) * u_hover;',
        '    float lensCompression = (0.011 + u_energy * 0.005) * lensFalloff;',
        '    samplePixel -= fromPointer * lensCompression;',
        '    samplePixel += pointerNormal * lensFalloff * (4.8 + u_energy * 3.8);',
        '    float velocityAmount = clamp(length(u_pointer_velocity), 0.0, 1.0);',
        '    samplePixel -= u_pointer_velocity * lensFalloff * (4.0 + velocityAmount * 4.5);',
        '    float rippleEnvelope = exp(-pointerDistance / (lensRadius * 1.15));',
        '    float ripple = sin(pointerDistance * 0.105 - u_time * 8.2);',
        '    samplePixel += pointerNormal * ripple * rippleEnvelope * u_impulse * 3.8;',

        '    vec2 blurX = vec2(u_blur, 0.0);',
        '    vec2 blurY = vec2(0.0, u_blur);',
        '    vec3 centerColor = sampleBackground(samplePixel);',
        '    vec3 softColor = centerColor * 0.44;',
        '    softColor += sampleBackground(samplePixel + blurX) * 0.14;',
        '    softColor += sampleBackground(samplePixel - blurX) * 0.14;',
        '    softColor += sampleBackground(samplePixel + blurY) * 0.14;',
        '    softColor += sampleBackground(samplePixel - blurY) * 0.14;',

        '    vec2 opticalNormal = normalize(edgeNormal * (0.72 + rim) + pointerNormal * lensFalloff * 0.52 + bodyField * bodyMask * 0.08);',
        '    float chromaDistance = u_dispersion * (0.12 + rim * 0.7 + lensFalloff * 0.24);',
        '    float red = sampleBackground(samplePixel + opticalNormal * chromaDistance).r;',
        '    float blue = sampleBackground(samplePixel - opticalNormal * chromaDistance).b;',
        '    vec3 chromatic = vec3(red, centerColor.g, blue);',
        '    float chromaMix = clamp(rim * 0.82 + lensFalloff * 0.18, 0.0, 0.88);',
        '    vec3 color = mix(softColor, chromatic, chromaMix);',
        '    color = coolCampusGrade(color, samplePixel);',

        '    vec2 lightVector = pointerPixel - p;',
        '    float lightLength = max(length(lightVector), 0.001);',
        '    vec2 lightDirection = lightVector / lightLength;',
        '    float directionalLight = pow(max(dot(edgeNormal, lightDirection), 0.0), 2.4);',
        '    float topLeftLight = pow(max(dot(edgeNormal, normalize(vec2(-0.72, -0.68))), 0.0), 2.0);',
        '    float lowerShadow = pow(max(dot(edgeNormal, normalize(vec2(0.68, 0.74))), 0.0), 1.7);',
        '    float specular = rim * (topLeftLight * 0.16 + directionalLight * u_hover * (0.08 + u_energy * 0.1));',
        '    float causticWave = 0.5 + 0.5 * sin(bodyField.x * 2.6 + bodyField.y * 2.1 + p.x * 0.018 - p.y * 0.012);',
        '    float bodyCaustic = pow(causticWave, 7.0) * bodyMask * (0.006 + lensFalloff * 0.012);',
        '    float edgeCaustic = (0.5 + 0.5 * sin(p.x * 0.045 + p.y * 0.022 + u_time * 1.8));',
        '    edgeCaustic *= rim * rim * (0.021 + u_energy * 0.025);',
        '    color += vec3(0.78, 0.92, 1.0) * (specular + bodyCaustic + edgeCaustic + rim * 0.022);',
        '    color -= vec3(0.025, 0.052, 0.078) * lowerShadow * rim * 0.24;',
        '    color *= 1.035;',

        '    gl_FragColor = vec4(clamp(color, 0.0, 1.0), 1.0);',
        '}'
    ].join('\n');

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
            var message = gl.getProgramInfoLog(program) || 'Unknown WebGL link error';
            gl.deleteProgram(program);
            throw new Error(message);
        }
        return program;
    }

    function clamp(value, minimum, maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    function mix(current, target, amount) {
        return current + (target - current) * amount;
    }

    function LiquidGlass(card) {
        this.card = card;
        this.canvas = card.querySelector('.login-liquid-canvas');
        this.backgroundUrl = card.getAttribute('data-liquid-background');
        this.variant = card.getAttribute('data-liquid-variant') === 'dark' ? 1 : 0;
        this.gl = null;
        this.program = null;
        this.texture = null;
        this.buffer = null;
        this.locations = null;
        this.image = null;
        this.cardRect = null;
        this.raf = 0;
        this.resizeRaf = 0;
        this.animateUntil = 0;
        this.lastFrameTime = 0;
        this.visible = true;
        this.destroyed = false;
        this.pointer = { x: 0.20, y: 0.10 };
        this.pointerTarget = { x: 0.20, y: 0.10 };
        this.pointerVelocity = { x: 0, y: 0 };
        this.pointerVelocityTarget = { x: 0, y: 0 };
        this.lastPointerSample = null;
        this.hover = 0;
        this.hoverTarget = 0;
        this.impulse = 0;
        this.energy = 0.12;
        this.energyTarget = 0.12;
        this.reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.forcedColors = window.matchMedia && window.matchMedia('(forced-colors: active)').matches;
        this.reducedTransparency = window.matchMedia && window.matchMedia('(prefers-reduced-transparency: reduce)').matches;
        this.handlePointerMove = this.handlePointerMove.bind(this);
        this.handlePointerEnter = this.handlePointerEnter.bind(this);
        this.handlePointerLeave = this.handlePointerLeave.bind(this);
        this.handlePointerDown = this.handlePointerDown.bind(this);
        this.handlePointerUp = this.handlePointerUp.bind(this);
        this.handleFocusIn = this.handleFocusIn.bind(this);
        this.handleFocusOut = this.handleFocusOut.bind(this);
        this.scheduleGeometry = this.scheduleGeometry.bind(this);
        this.handleVisibility = this.handleVisibility.bind(this);
        this.frame = this.frame.bind(this);
    }

    LiquidGlass.prototype.init = function () {
        var self = this;
        if (!this.canvas || !this.backgroundUrl || this.forcedColors || this.reducedTransparency) {
            return;
        }

        try {
            this.gl = this.canvas.getContext('webgl', {
                alpha: true,
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
                self.syncGeometry();
                self.card.classList.add('is-liquid-webgl-ready');
                self.card.setAttribute('data-liquid-engine', 'webgl');
                self.requestDraw(320);
            } catch (error) {
                self.fail(error);
            }
        };
        this.image.onerror = function () {
            self.fail(new Error('Liquid glass background image could not be loaded'));
        };
        this.image.src = this.backgroundUrl;
    };

    LiquidGlass.prototype.setupGeometry = function () {
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

    LiquidGlass.prototype.cacheLocations = function () {
        var gl = this.gl;
        var program = this.program;
        this.locations = {
            position: gl.getAttribLocation(program, 'a_position'),
            uv: gl.getAttribLocation(program, 'a_uv'),
            background: gl.getUniformLocation(program, 'u_background'),
            viewport: gl.getUniformLocation(program, 'u_viewport'),
            imageSize: gl.getUniformLocation(program, 'u_image_size'),
            cardOrigin: gl.getUniformLocation(program, 'u_card_origin'),
            cardSize: gl.getUniformLocation(program, 'u_card_size'),
            pointer: gl.getUniformLocation(program, 'u_pointer'),
            radius: gl.getUniformLocation(program, 'u_radius'),
            edgeWidth: gl.getUniformLocation(program, 'u_edge_width'),
            refraction: gl.getUniformLocation(program, 'u_refraction'),
            dispersion: gl.getUniformLocation(program, 'u_dispersion'),
            blur: gl.getUniformLocation(program, 'u_blur'),
            energy: gl.getUniformLocation(program, 'u_energy'),
            time: gl.getUniformLocation(program, 'u_time'),
            variant: gl.getUniformLocation(program, 'u_variant'),
            pointerVelocity: gl.getUniformLocation(program, 'u_pointer_velocity'),
            hover: gl.getUniformLocation(program, 'u_hover'),
            impulse: gl.getUniformLocation(program, 'u_impulse')
        };
    };

    LiquidGlass.prototype.setupTexture = function () {
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

    LiquidGlass.prototype.bindEvents = function () {
        var self = this;
        this.card.addEventListener('pointermove', this.handlePointerMove, { passive: true });
        this.card.addEventListener('pointerenter', this.handlePointerEnter, { passive: true });
        this.card.addEventListener('pointerleave', this.handlePointerLeave, { passive: true });
        this.card.addEventListener('pointerdown', this.handlePointerDown, { passive: true });
        window.addEventListener('pointerup', this.handlePointerUp, { passive: true });
        this.card.addEventListener('focusin', this.handleFocusIn);
        this.card.addEventListener('focusout', this.handleFocusOut);
        window.addEventListener('resize', this.scheduleGeometry, { passive: true });
        window.addEventListener('scroll', this.scheduleGeometry, { passive: true });
        document.addEventListener('visibilitychange', this.handleVisibility);

        if ('ResizeObserver' in window) {
            this.resizeObserver = new ResizeObserver(function () {
                self.scheduleGeometry();
            });
            this.resizeObserver.observe(this.card);
        }

        if ('IntersectionObserver' in window) {
            this.intersectionObserver = new IntersectionObserver(function (entries) {
                self.visible = Boolean(entries[0] && entries[0].isIntersecting);
                if (self.visible) {
                    self.requestDraw(80);
                }
            }, { threshold: 0.01 });
            this.intersectionObserver.observe(this.card);
        }

        this.canvas.addEventListener('webglcontextlost', function (event) {
            event.preventDefault();
            self.card.classList.remove('is-liquid-webgl-ready');
            self.card.removeAttribute('data-liquid-engine');
            if (self.raf) {
                cancelAnimationFrame(self.raf);
                self.raf = 0;
            }
        });
        this.canvas.addEventListener('webglcontextrestored', function () {
            self.card.classList.remove('is-liquid-webgl-ready');
            self.card.removeAttribute('data-liquid-engine');
        });
    };

    LiquidGlass.prototype.syncGeometry = function () {
        if (!this.gl || !this.image) {
            return;
        }
        this.cardRect = this.card.getBoundingClientRect();
        var mobile = window.innerWidth <= 640;
        var dprLimit = mobile ? 1 : 1.5;
        var pixelRatio = Math.min(window.devicePixelRatio || 1, dprLimit);
        var width = Math.max(1, Math.round(this.cardRect.width * pixelRatio));
        var height = Math.max(1, Math.round(this.cardRect.height * pixelRatio));
        if (this.canvas.width !== width || this.canvas.height !== height) {
            this.canvas.width = width;
            this.canvas.height = height;
        }
        this.gl.viewport(0, 0, width, height);
    };

    LiquidGlass.prototype.scheduleGeometry = function () {
        var self = this;
        if (this.resizeRaf || this.destroyed) {
            return;
        }
        this.resizeRaf = requestAnimationFrame(function () {
            self.resizeRaf = 0;
            self.syncGeometry();
            self.requestDraw(120);
        });
    };

    LiquidGlass.prototype.setPointerFromEvent = function (event) {
        var rect = this.cardRect || this.card.getBoundingClientRect();
        this.pointerTarget.x = clamp((event.clientX - rect.left) / rect.width, 0, 1);
        this.pointerTarget.y = clamp((event.clientY - rect.top) / rect.height, 0, 1);
    };

    LiquidGlass.prototype.setPointerFromElement = function (element) {
        var cardRect = this.cardRect || this.card.getBoundingClientRect();
        var rect = element.getBoundingClientRect();
        this.pointerTarget.x = clamp((rect.left + rect.width * 0.5 - cardRect.left) / cardRect.width, 0, 1);
        this.pointerTarget.y = clamp((rect.top + rect.height * 0.5 - cardRect.top) / cardRect.height, 0, 1);
    };

    LiquidGlass.prototype.capturePointerVelocity = function (event) {
        var now = performance.now();
        if (this.lastPointerSample) {
            var deltaTime = Math.max(8, now - this.lastPointerSample.time);
            var velocityX = (event.clientX - this.lastPointerSample.x) / deltaTime;
            var velocityY = (event.clientY - this.lastPointerSample.y) / deltaTime;
            this.pointerVelocityTarget.x = clamp(velocityX / 1.15, -1, 1);
            this.pointerVelocityTarget.y = clamp(velocityY / 1.15, -1, 1);
            var speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            this.impulse = Math.max(this.impulse, clamp(speed * 0.16, 0, 0.38));
        }
        this.lastPointerSample = { x: event.clientX, y: event.clientY, time: now };
    };

    LiquidGlass.prototype.handlePointerMove = function (event) {
        if (this.reducedMotion) {
            return;
        }
        this.capturePointerVelocity(event);
        this.setPointerFromEvent(event);
        this.hoverTarget = 1;
        this.energyTarget = Math.max(this.energyTarget, 0.56);
        this.requestDraw(900);
    };

    LiquidGlass.prototype.handlePointerEnter = function (event) {
        this.setPointerFromEvent(event);
        this.lastPointerSample = { x: event.clientX, y: event.clientY, time: performance.now() };
        this.hoverTarget = 1;
        this.energyTarget = 0.58;
        this.card.classList.add('is-liquid-hovered');
        this.requestDraw(900);
    };

    LiquidGlass.prototype.handlePointerLeave = function () {
        this.lastPointerSample = null;
        this.pointerVelocityTarget.x = 0;
        this.pointerVelocityTarget.y = 0;
        this.hoverTarget = this.card.contains(document.activeElement) ? 0.58 : 0;
        this.energyTarget = this.card.contains(document.activeElement) ? 0.72 : 0.12;
        this.card.classList.remove('is-liquid-hovered');
        this.requestDraw(1100);
    };

    LiquidGlass.prototype.handlePointerDown = function (event) {
        this.setPointerFromEvent(event);
        this.hoverTarget = 1;
        this.energyTarget = 1;
        this.impulse = 1;
        this.card.classList.add('is-liquid-pressed');
        this.requestDraw(1200);
    };

    LiquidGlass.prototype.handlePointerUp = function () {
        this.card.classList.remove('is-liquid-pressed');
        this.energyTarget = this.card.contains(document.activeElement) ? 0.72 : 0.42;
        this.requestDraw(1000);
    };

    LiquidGlass.prototype.handleFocusIn = function (event) {
        this.setPointerFromElement(event.target);
        this.hoverTarget = this.card.matches(':hover') ? 1 : 0.58;
        this.energyTarget = 0.78;
        this.card.classList.add('is-liquid-focused');
        this.requestDraw(1000);
    };

    LiquidGlass.prototype.handleFocusOut = function () {
        var self = this;
        window.setTimeout(function () {
            if (!self.card.contains(document.activeElement)) {
                self.card.classList.remove('is-liquid-focused');
                self.energyTarget = self.card.matches(':hover') ? 0.42 : 0.12;
                self.hoverTarget = self.card.matches(':hover') ? 1 : 0;
                self.requestDraw(1000);
            }
        }, 0);
    };

    LiquidGlass.prototype.handleVisibility = function () {
        if (document.hidden) {
            if (this.raf) {
                cancelAnimationFrame(this.raf);
                this.raf = 0;
            }
            return;
        }
        this.requestDraw(100);
    };

    LiquidGlass.prototype.requestDraw = function (duration) {
        if (this.destroyed || !this.visible || document.hidden || !this.gl || !this.texture) {
            return;
        }
        var now = performance.now();
        this.animateUntil = Math.max(this.animateUntil, now + (this.reducedMotion ? 0 : duration));
        if (!this.raf) {
            this.raf = requestAnimationFrame(this.frame);
        }
    };

    LiquidGlass.prototype.frame = function (now) {
        this.raf = 0;
        if (this.destroyed || !this.visible || document.hidden) {
            return;
        }
        var active = this.hover > 0.01 || this.hoverTarget > 0.01 || this.impulse > 0.01;
        var minimumFrameInterval = active ? 16 : 66;
        if (!this.reducedMotion && this.lastFrameTime && now - this.lastFrameTime < minimumFrameInterval) {
            this.raf = requestAnimationFrame(this.frame);
            return;
        }
        var elapsed = this.lastFrameTime ? Math.min((now - this.lastFrameTime) / 16.667, 4) : 1;
        this.lastFrameTime = now;
        var amount = this.reducedMotion ? 1 : 1 - Math.pow(0.82, elapsed);
        var velocityAmount = this.reducedMotion ? 1 : 1 - Math.pow(0.72, elapsed);
        this.pointer.x = mix(this.pointer.x, this.pointerTarget.x, amount);
        this.pointer.y = mix(this.pointer.y, this.pointerTarget.y, amount);
        this.energy = mix(this.energy, this.energyTarget, amount);
        this.hover = mix(this.hover, this.hoverTarget, amount);
        this.pointerVelocity.x = mix(this.pointerVelocity.x, this.pointerVelocityTarget.x, velocityAmount);
        this.pointerVelocity.y = mix(this.pointerVelocity.y, this.pointerVelocityTarget.y, velocityAmount);
        this.pointerVelocityTarget.x *= Math.pow(0.78, elapsed);
        this.pointerVelocityTarget.y *= Math.pow(0.78, elapsed);
        this.impulse *= Math.pow(0.9, elapsed);
        this.draw(now * 0.001);

        var unsettled = Math.abs(this.pointer.x - this.pointerTarget.x) > 0.001
            || Math.abs(this.pointer.y - this.pointerTarget.y) > 0.001
            || Math.abs(this.energy - this.energyTarget) > 0.002
            || Math.abs(this.hover - this.hoverTarget) > 0.002
            || Math.abs(this.pointerVelocity.x) > 0.002
            || Math.abs(this.pointerVelocity.y) > 0.002
            || this.impulse > 0.002;
        if (!this.reducedMotion && (now < this.animateUntil || unsettled || this.visible)) {
            this.raf = requestAnimationFrame(this.frame);
        }
    };

    LiquidGlass.prototype.draw = function (time) {
        var gl = this.gl;
        var rect = this.cardRect || this.card.getBoundingClientRect();
        var mobile = window.innerWidth <= 640;
        var radius = parseFloat(getComputedStyle(this.card).borderTopLeftRadius) || (mobile ? 22 : 30);
        var refraction = mobile ? 10 : 14;
        var edgeWidth = mobile ? 20 : 24;
        var dispersion = mobile ? 0.65 : 0.9;
        var blur = (mobile ? 1.25 : 1.65) + this.variant * 0.3;

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
        gl.uniform2f(this.locations.cardOrigin, rect.left, rect.top);
        gl.uniform2f(this.locations.cardSize, rect.width, rect.height);
        gl.uniform2f(this.locations.pointer, this.pointer.x, this.pointer.y);
        gl.uniform1f(this.locations.radius, radius);
        gl.uniform1f(this.locations.edgeWidth, edgeWidth);
        gl.uniform1f(this.locations.refraction, refraction * (1 + this.energy * 0.16));
        gl.uniform1f(this.locations.dispersion, dispersion * (1 + this.energy * 0.22));
        gl.uniform1f(this.locations.blur, blur);
        gl.uniform1f(this.locations.energy, this.energy);
        gl.uniform1f(this.locations.time, time);
        gl.uniform1f(this.locations.variant, this.variant);
        gl.uniform2f(this.locations.pointerVelocity, this.pointerVelocity.x, this.pointerVelocity.y);
        gl.uniform1f(this.locations.hover, this.hover);
        gl.uniform1f(this.locations.impulse, this.impulse);
        gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    };

    LiquidGlass.prototype.fail = function (error) {
        this.card.classList.remove('is-liquid-webgl-ready');
        this.card.removeAttribute('data-liquid-engine');
        if (window.console && console.warn) {
            console.warn('[login-liquid-glass] WebGL enhancement disabled:', error && error.message ? error.message : error);
        }
    };

    function initLiquidGlass() {
        var cards = document.querySelectorAll('[data-liquid-glass]');
        for (var index = 0; index < cards.length; index += 1) {
            var instance = new LiquidGlass(cards[index]);
            instance.init();
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initLiquidGlass);
    } else {
        initLiquidGlass();
    }
}());
