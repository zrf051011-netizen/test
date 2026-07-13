        </main>
    </section>
</div>

<div class="modal-backdrop" id="passwordModal" hidden>
    <div class="modal">
        <div class="modal-head">
            <h2>修改密码</h2>
            <button class="icon-btn" data-close-password>×</button>
        </div>
        <form id="passwordForm" class="form-grid single">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <label>
                <span>原密码</span>
                <div class="password-field">
                    <input type="password" name="oldPassword" required>
                    <button type="button" data-toggle-password>显示</button>
                </div>
            </label>
            <label>
                <span>新密码</span>
                <div class="password-field">
                    <input type="password" name="newPassword" required minlength="6">
                    <button type="button" data-toggle-password>显示</button>
                </div>
            </label>
            <label>
                <span>确认新密码</span>
                <div class="password-field">
                    <input type="password" name="confirmPassword" required minlength="6">
                    <button type="button" data-toggle-password>显示</button>
                </div>
            </label>
            <div class="form-actions">
                <button type="button" class="ghost-btn" data-close-password>取消</button>
                <button type="submit" class="primary-btn">保存</button>
            </div>
            <p class="form-message" id="passwordMessage"></p>
        </form>
    </div>
</div>

<script>
    window.APP_CONTEXT = '${ctx}';
</script>
<script src="${ctx}/js/app.js?v=202607101620"></script>
<script src="${ctx}/js/app-liquid-glass.js?v=202607130200"></script>
</body>
</html>
