(function () {
    var modal = document.getElementById('passwordModal');
    var form = document.getElementById('passwordForm');
    var message = document.getElementById('passwordMessage');
    var studentImportForm = document.getElementById('studentImportForm');
    var studentImportFile = studentImportForm ? studentImportForm.querySelector('[data-import-file]') : null;
    var studentImportSubmit = studentImportForm ? studentImportForm.querySelector('[data-import-submit]') : null;
    var studentImportFilename = studentImportForm ? studentImportForm.querySelector('[data-import-file-name]') : null;
    var studentImportPicker = studentImportForm ? studentImportForm.querySelector('[data-import-picker]') : null;
    var noticeRoot = document.querySelector('[data-notice-root]');
    var noticeToggle = noticeRoot ? noticeRoot.querySelector('[data-notice-toggle]') : null;
    var noticePanel = noticeRoot ? noticeRoot.querySelector('[data-notice-panel]') : null;
    var forgotModal = document.getElementById('forgotPasswordModal');
    var forgotForm = document.getElementById('forgotPasswordForm');
    var forgotMessage = document.getElementById('forgotPasswordMessage');
    var forgotSubmit = forgotForm ? forgotForm.querySelector('[data-forgot-submit]') : null;
    var loginUsername = document.querySelector('.login-form [name="username"]');

    function setNoticeOpen(open) {
        if (!noticeToggle || !noticePanel) {
            return;
        }
        noticePanel.hidden = !open;
        noticeToggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    }

    function setForgotOpen(open) {
        if (!forgotModal) {
            return;
        }
        forgotModal.hidden = !open;
        document.body.classList.toggle('modal-open', open);
        if (forgotMessage) {
            forgotMessage.textContent = '';
            forgotMessage.removeAttribute('data-state');
        }
        if (open && forgotForm) {
            var usernameField = forgotForm.querySelector('[name="username"]');
            if (usernameField && loginUsername) {
                usernameField.value = loginUsername.value;
            }
            setTimeout(function () {
                var focusTarget = usernameField && usernameField.value ? forgotForm.querySelector('[name="phone"]') : usernameField;
                if (focusTarget) {
                    focusTarget.focus();
                }
            }, 0);
        }
    }

    if (studentImportForm && window.URLSearchParams && new URLSearchParams(window.location.search).get('import') === '1') {
        studentImportForm.hidden = false;
        var autoOpenTrigger = document.querySelector('[data-open-student-import]');
        if (autoOpenTrigger) {
            autoOpenTrigger.setAttribute('aria-expanded', 'true');
        }
        setTimeout(function () {
            studentImportForm.scrollIntoView({block: 'center'});
        }, 0);
    }

    document.addEventListener('click', function (event) {
        var clickedNoticeToggle = event.target.closest ? event.target.closest('[data-notice-toggle]') : null;
        if (clickedNoticeToggle && noticeRoot && noticeRoot.contains(clickedNoticeToggle)) {
            setNoticeOpen(clickedNoticeToggle.getAttribute('aria-expanded') !== 'true');
        } else if (noticeRoot && !noticeRoot.contains(event.target)) {
            setNoticeOpen(false);
        }

        var openForgot = event.target.closest ? event.target.closest('[data-open-forgot]') : null;
        if (openForgot) {
            setForgotOpen(true);
        }
        var closeForgot = event.target.closest ? event.target.closest('[data-close-forgot]') : null;
        if (closeForgot || (forgotModal && event.target === forgotModal)) {
            setForgotOpen(false);
        }

        var studentStatusButton = event.target.closest ? event.target.closest('[data-student-status]') : null;
        if (studentStatusButton) {
            var statusForm = studentStatusButton.closest('form');
            var statusInput = statusForm ? statusForm.querySelector('[data-student-status-input]') : null;
            if (statusForm && statusInput) {
                statusInput.value = studentStatusButton.getAttribute('data-student-status') || '';
                statusForm.submit();
            }
        }
        var openStudentImport = event.target.closest ? event.target.closest('[data-open-student-import]') : null;
        if (openStudentImport && studentImportForm) {
            studentImportForm.hidden = false;
            openStudentImport.setAttribute('aria-expanded', 'true');
        }
        var closeStudentImport = event.target.closest ? event.target.closest('[data-close-student-import]') : null;
        if (closeStudentImport && studentImportForm) {
            studentImportForm.reset();
            studentImportForm.hidden = true;
            if (studentImportSubmit) {
                studentImportSubmit.disabled = true;
            }
            if (studentImportFilename) {
                studentImportFilename.textContent = '选择 Excel 文件';
            }
            if (studentImportPicker) {
                studentImportPicker.classList.remove('is-selected');
            }
            var importTrigger = document.querySelector('[data-open-student-import]');
            if (importTrigger) {
                importTrigger.setAttribute('aria-expanded', 'false');
            }
        }
        var openPassword = event.target.closest ? event.target.closest('[data-open-password]') : null;
        if (openPassword && modal) {
            modal.hidden = false;
            if (message) {
                message.textContent = '';
            }
        }
        var closePassword = event.target.closest ? event.target.closest('[data-close-password]') : null;
        if (closePassword && modal) {
            modal.hidden = true;
            if (message) {
                message.textContent = '';
            }
        }
        var confirmTarget = event.target.closest ? event.target.closest('[data-confirm]') : null;
        if (confirmTarget) {
            var text = confirmTarget.getAttribute('data-confirm') || '确认执行该操作？';
            if (!window.confirm(text)) {
                event.preventDefault();
            }
        }
        var togglePassword = event.target.closest ? event.target.closest('[data-toggle-password]') : null;
        if (togglePassword) {
            var wrapper = togglePassword.closest('.password-field');
            var input = wrapper ? wrapper.querySelector('input') : null;
            if (!input) {
                return;
            }
            var shouldShow = input.type === 'password';
            input.type = shouldShow ? 'text' : 'password';
            togglePassword.textContent = shouldShow ? '隐藏' : '显示';
        }
    });

    document.addEventListener('keydown', function (event) {
        if (event.key !== 'Escape') {
            return;
        }
        setNoticeOpen(false);
        if (forgotModal && !forgotModal.hidden) {
            setForgotOpen(false);
        }
    });

    if (studentImportFile) {
        studentImportFile.addEventListener('change', function () {
            var file = studentImportFile.files && studentImportFile.files[0];
            if (studentImportFilename) {
                studentImportFilename.textContent = file ? file.name : '选择 Excel 文件';
            }
            if (studentImportSubmit) {
                studentImportSubmit.disabled = !file;
            }
            if (studentImportPicker) {
                studentImportPicker.classList.toggle('is-selected', !!file);
            }
        });
    }

    if (studentImportForm) {
        studentImportForm.addEventListener('submit', function () {
            if (studentImportSubmit) {
                studentImportSubmit.disabled = true;
                studentImportSubmit.textContent = '正在导入...';
            }
        });
    }

    if (forgotForm) {
        forgotForm.addEventListener('submit', function (event) {
            event.preventDefault();
            if (forgotSubmit) {
                forgotSubmit.disabled = true;
                forgotSubmit.textContent = '正在验证...';
            }
            if (forgotMessage) {
                forgotMessage.textContent = '';
                forgotMessage.removeAttribute('data-state');
            }

            var submittedUsername = forgotForm.querySelector('[name="username"]').value;
            var body = new URLSearchParams(new FormData(forgotForm));
            fetch((window.APP_CONTEXT || '') + '/forgot-password', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
                body: body.toString()
            })
                .then(function (response) { return response.json(); })
                .then(function (result) {
                    if (!forgotMessage) {
                        return;
                    }
                    forgotMessage.textContent = result.message;
                    forgotMessage.setAttribute('data-state', result.success ? 'success' : 'error');
                    if (result.success) {
                        if (loginUsername) {
                            loginUsername.value = submittedUsername;
                        }
                        forgotForm.reset();
                    }
                })
                .catch(function () {
                    if (forgotMessage) {
                        forgotMessage.textContent = '请求失败，请稍后重试';
                        forgotMessage.setAttribute('data-state', 'error');
                    }
                })
                .finally(function () {
                    if (forgotSubmit) {
                        forgotSubmit.disabled = false;
                        forgotSubmit.textContent = '确认重置';
                    }
                });
        });
    }

    if (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            var body = new URLSearchParams(new FormData(form));
            fetch((window.APP_CONTEXT || '') + '/user/changePwd', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
                body: body.toString()
            })
                .then(function (response) { return response.json(); })
                .then(function (result) {
                    message.textContent = result.message;
                    message.style.color = result.success ? '#047857' : '#b42318';
                    if (result.success) {
                        form.reset();
                        setTimeout(function () {
                            window.location.href = result.redirectUrl || (window.APP_CONTEXT || '') + '/';
                        }, 600);
                    }
                })
                .catch(function () {
                    message.textContent = '请求失败，请稍后重试';
                    message.style.color = '#b42318';
                });
        });
    }
})();
