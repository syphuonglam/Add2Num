(function () {
    const translations = {
        vi: {
            pageTitle: 'Bảng cộng Add2Num',
            languageLabel: 'Ngôn ngữ',
            eyebrow: 'Add2Num / Lớp học số',
            title: 'Cùng đặt tính rồi cộng.',
            intro: 'Nhập hai số lớn, gọi phép tính, sau đó đi từng cột từ hàng đơn vị sang bên trái.',
            tag: 'Học bằng từng bước',
            prepareTitle: 'Chuẩn bị phép tính',
            panelCopy: 'Chỉ dùng chữ số không âm. Số dài cũng được.',
            firstNumber: 'Số thứ nhất',
            secondNumber: 'Số thứ hai',
            firstPlaceholder: 'Ví dụ: 485',
            secondPlaceholder: 'Ví dụ: 267',
            start: 'Bắt đầu cộng',
            calculating: 'Đang tính...',
            previous: '◀ Bước trước',
            next: 'Bước tiếp theo ▶',
            boardEyebrow: 'Bảng đặt tính',
            boardTitle: 'Nhìn rõ từng hàng',
            notStarted: 'Chưa bắt đầu',
            preparing: 'Đang chuẩn bị',
            initialInstruction: 'Nhập hai số rồi bấm “Bắt đầu cộng”.',
            apiChecking: 'Đang gọi API để kiểm tra kết quả...',
            readyInstruction: 'Phép tính đã lên bảng. Bấm “Bước tiếp theo” để bắt đầu từ hàng đơn vị.',
            result: 'Kết quả:',
            stepCount: '{current} / {total} bước',
            stepLabel: 'Bước {number}: ',
            completed: 'Hoàn thành! Em đã cộng lần lượt từ hàng đơn vị sang bên trái.',
            apiError: 'Không thể thực hiện phép cộng.',
            resultMismatch: 'Kết quả hiển thị không khớp với API.',
            fields: {
                a: 'Số thứ nhất',
                b: 'Số thứ hai'
            },
            errors: {
                null: '{field} không được để trống.',
                blank: '{field} không được để trống.',
                required: 'Vui lòng nhập {field}.',
                string: '{field} phải là chuỗi chữ số.',
                negative: '{field} phải là số nguyên không âm.',
                digits: '{field} chỉ được chứa chữ số 0-9.',
                tooLarge: 'Số nhập vào vượt quá độ dài cho phép.',
                bodyTooLarge: 'Dữ liệu gửi lên vượt quá kích thước cho phép.',
                malformed: 'Dữ liệu gửi lên không đúng định dạng.',
                unsupportedMedia: 'Định dạng gửi lên không được hỗ trợ.',
                notAcceptable: 'Định dạng phản hồi không được hỗ trợ.',
                generic: 'Đã xảy ra lỗi. Vui lòng thử lại.'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `Lấy ${first} cộng ${second}${carryText} bằng ${total}. Viết ${digit}${carrySentence}`,
            carryText: ({ carry }) => ` cộng nhớ ${carry}`,
            carrySentence: ({ carry }) => carry ? `, nhớ ${carry}.` : '.',
            finalCarry: ({ carry }) => `Hạ số nhớ ${carry} xuống kết quả.`
        },
        en: {
            pageTitle: 'Add2Num Addition Board',
            languageLabel: 'Language',
            eyebrow: 'Add2Num / Digital classroom',
            title: 'Set it up, then add.',
            intro: 'Enter two large numbers, check the calculation, then move column by column from the ones place.',
            tag: 'Learn step by step',
            prepareTitle: 'Prepare the calculation',
            panelCopy: 'Use non-negative digits only. Long numbers are welcome.',
            firstNumber: 'First number',
            secondNumber: 'Second number',
            firstPlaceholder: 'Example: 485',
            secondPlaceholder: 'Example: 267',
            start: 'Start adding',
            calculating: 'Calculating...',
            previous: '◀ Previous step',
            next: 'Next step ▶',
            boardEyebrow: 'Addition board',
            boardTitle: 'See each place clearly',
            notStarted: 'Not started',
            preparing: 'Preparing',
            initialInstruction: 'Enter two numbers and press “Start adding”.',
            apiChecking: 'Checking the result with the API...',
            readyInstruction: 'The calculation is ready. Press “Next step” to start at the ones place.',
            result: 'Result:',
            stepCount: '{current} / {total} steps',
            stepLabel: 'Step {number}: ',
            completed: 'Done! You added each place from right to left.',
            apiError: 'The addition could not be completed.',
            resultMismatch: 'The displayed result does not match the API.',
            fields: { a: 'The first number', b: 'The second number' },
            errors: {
                null: '{field} must not be empty.', blank: '{field} must not be empty.', required: 'Please enter {field}.',
                string: '{field} must be a string of digits.', negative: '{field} must be a non-negative integer.',
                digits: '{field} may contain only digits 0-9.', tooLarge: 'The number is longer than allowed.',
                bodyTooLarge: 'The request is larger than allowed.', malformed: 'The request format is invalid.',
                unsupportedMedia: 'This request format is not supported.', notAcceptable: 'This response format is not supported.',
                generic: 'Something went wrong. Please try again.'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `Add ${first} and ${second}${carryText} to get ${total}. Write ${digit}${carrySentence}`,
            carryText: ({ carry }) => ` plus carry ${carry}`,
            carrySentence: ({ carry }) => carry ? `, carry ${carry}.` : '.',
            finalCarry: ({ carry }) => `Bring the carry ${carry} down to the result.`
        },
        ko: {
            pageTitle: 'Add2Num 덧셈 보드', languageLabel: '언어', eyebrow: 'Add2Num / 디지털 교실',
            title: '세로로 놓고 더해요.', intro: '큰 두 수를 입력하고 일의 자리부터 한 칸씩 계산해 보세요.', tag: '한 단계씩 배우기',
            prepareTitle: '계산 준비', panelCopy: '음수가 아닌 숫자만 입력하세요. 긴 숫자도 사용할 수 있어요.',
            firstNumber: '첫 번째 수', secondNumber: '두 번째 수', firstPlaceholder: '예: 485', secondPlaceholder: '예: 267',
            start: '덧셈 시작', calculating: '계산 중...', previous: '◀ 이전 단계', next: '다음 단계 ▶', boardEyebrow: '덧셈 보드',
            boardTitle: '각 자리를 살펴봐요', notStarted: '시작 전', preparing: '준비 중',
            initialInstruction: '두 수를 입력하고 “덧셈 시작”을 누르세요.', apiChecking: 'API로 결과를 확인하는 중...',
            readyInstruction: '계산이 준비되었습니다. “다음 단계”를 눌러 일의 자리부터 시작하세요.', result: '결과:',
            stepCount: '{current} / {total}단계', stepLabel: '{number}단계: ', completed: '완료했어요! 오른쪽에서 왼쪽으로 계산했습니다.',
            apiError: '덧셈을 완료할 수 없습니다.', resultMismatch: '표시된 결과가 API 결과와 다릅니다.',
            fields: { a: '첫 번째 수', b: '두 번째 수' },
            errors: {
                null: '{field}을(를) 입력하세요.', blank: '{field}을(를) 입력하세요.', required: '{field}을(를) 입력하세요.',
                string: '{field}은(는) 숫자 문자열이어야 합니다.', negative: '{field}은(는) 음수가 아닌 정수여야 합니다.',
                digits: '{field}에는 0-9 숫자만 입력하세요.', tooLarge: '입력한 수가 허용된 길이보다 깁니다.',
                bodyTooLarge: '요청 데이터가 너무 큽니다.', malformed: '요청 형식이 올바르지 않습니다.',
                unsupportedMedia: '지원하지 않는 요청 형식입니다.', notAcceptable: '지원하지 않는 응답 형식입니다.',
                generic: '오류가 발생했습니다. 다시 시도하세요.'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `${first} 더하기 ${second}${carryText}은(는) ${total}입니다. ${digit}을(를) 쓰세요${carrySentence}`,
            carryText: ({ carry }) => `, 받아올림 ${carry}`,
            carrySentence: ({ carry }) => carry ? `, ${carry}을(를) 받아올림합니다.` : '.',
            finalCarry: ({ carry }) => `받아올림한 ${carry}을(를) 결과에 씁니다.`
        },
        ja: {
            pageTitle: 'Add2Num たし算ボード', languageLabel: '言語', eyebrow: 'Add2Num / デジタル教室',
            title: 'たてに並べて、たし算しよう。', intro: '大きな数を2つ入力し、一の位から左へ順番に計算します。', tag: '一歩ずつ学ぼう',
            prepareTitle: '計算の準備', panelCopy: '0から9の数字だけを入力してください。大きな数にも対応しています。',
            firstNumber: '1つ目の数', secondNumber: '2つ目の数', firstPlaceholder: '例: 485', secondPlaceholder: '例: 267',
            start: 'たし算を始める', calculating: '計算中...', previous: '◀ 前のステップ', next: '次のステップ ▶', boardEyebrow: 'たし算ボード',
            boardTitle: '位ごとに見てみよう', notStarted: '未開始', preparing: '準備中',
            initialInstruction: '2つの数を入力して「たし算を始める」を押してください。', apiChecking: 'APIで答えを確認しています...',
            readyInstruction: '計算の準備ができました。「次のステップ」で一の位から始めます。', result: '答え:',
            stepCount: '{current} / {total} ステップ', stepLabel: '{number}ステップ: ', completed: '完成です！右から左へ順番にたしました。',
            apiError: 'たし算を完了できませんでした。', resultMismatch: '表示された答えがAPIの答えと一致しません。',
            fields: { a: '1つ目の数', b: '2つ目の数' },
            errors: {
                null: '{field}を入力してください。', blank: '{field}を入力してください。', required: '{field}を入力してください。',
                string: '{field}は数字の文字列で入力してください。', negative: '{field}は0以上の整数にしてください。',
                digits: '{field}には0から9の数字だけを使ってください。', tooLarge: '入力した数が長すぎます。',
                bodyTooLarge: '送信データが大きすぎます。', malformed: '送信データの形式が正しくありません。',
                unsupportedMedia: '対応していない送信形式です。', notAcceptable: '対応していない応答形式です。',
                generic: 'エラーが発生しました。もう一度お試しください。'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `${first}たす${second}${carryText}は${total}です。${digit}を書きます${carrySentence}`,
            carryText: ({ carry }) => `、くり上がり${carry}`, carrySentence: ({ carry }) => carry ? `、${carry}くり上がります。` : '。',
            finalCarry: ({ carry }) => `くり上がりの${carry}を答えに書きます。`
        },
        zh: {
            pageTitle: 'Add2Num 加法板', languageLabel: '语言', eyebrow: 'Add2Num / 数字课堂',
            title: '竖式排列，一步一步相加。', intro: '输入两个大数，从个位开始逐列向左计算。', tag: '一步一步学习',
            prepareTitle: '准备计算', panelCopy: '只输入非负数字。支持很长的数字。',
            firstNumber: '第一个数', secondNumber: '第二个数', firstPlaceholder: '例如：485', secondPlaceholder: '例如：267',
            start: '开始相加', calculating: '计算中...', previous: '◀ 上一步', next: '下一步 ▶', boardEyebrow: '加法板',
            boardTitle: '看清每一位', notStarted: '尚未开始', preparing: '准备中',
            initialInstruction: '输入两个数，然后点击“开始相加”。', apiChecking: '正在通过 API 检查结果...',
            readyInstruction: '计算已准备好。点击“下一步”从个位开始。', result: '结果:',
            stepCount: '{current} / {total} 步', stepLabel: '第 {number} 步：', completed: '完成！已经从右到左计算每一位。',
            apiError: '无法完成加法。', resultMismatch: '显示的结果与 API 结果不一致。',
            fields: { a: '第一个数', b: '第二个数' },
            errors: {
                null: '请输入{field}。', blank: '请输入{field}。', required: '请输入{field}。',
                string: '{field}必须是数字字符串。', negative: '{field}必须是非负整数。',
                digits: '{field}只能包含0到9的数字。', tooLarge: '输入的数字超过允许长度。',
                bodyTooLarge: '请求数据超过允许大小。', malformed: '请求格式不正确。',
                unsupportedMedia: '不支持此请求格式。', notAcceptable: '不支持此响应格式。',
                generic: '发生错误，请重试。'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `${first}加${second}${carryText}等于${total}。写下${digit}${carrySentence}`,
            carryText: ({ carry }) => `，进位${carry}`, carrySentence: ({ carry }) => carry ? `，进${carry}位。` : '。',
            finalCarry: ({ carry }) => `把进位${carry}写到结果中。`
        },
        es: {
            pageTitle: 'Pizarra de sumas Add2Num', languageLabel: 'Idioma', eyebrow: 'Add2Num / Aula digital',
            title: 'Coloca y suma paso a paso.', intro: 'Escribe dos números grandes y calcula columna por columna desde las unidades.', tag: 'Aprende paso a paso',
            prepareTitle: 'Prepara la suma', panelCopy: 'Usa solo dígitos no negativos. También puedes usar números largos.',
            firstNumber: 'Primer número', secondNumber: 'Segundo número', firstPlaceholder: 'Ejemplo: 485', secondPlaceholder: 'Ejemplo: 267',
            start: 'Empezar suma', calculating: 'Calculando...', previous: '◀ Paso anterior', next: 'Siguiente paso ▶', boardEyebrow: 'Pizarra de suma',
            boardTitle: 'Mira cada posición', notStarted: 'Sin empezar', preparing: 'Preparando',
            initialInstruction: 'Escribe dos números y pulsa “Empezar suma”.', apiChecking: 'Comprobando el resultado con la API...',
            readyInstruction: 'La suma está lista. Pulsa “Siguiente paso” para empezar por las unidades.', result: 'Resultado:',
            stepCount: '{current} / {total} pasos', stepLabel: 'Paso {number}: ', completed: '¡Listo! Has sumado cada posición de derecha a izquierda.',
            apiError: 'No se pudo completar la suma.', resultMismatch: 'El resultado mostrado no coincide con la API.',
            fields: { a: 'el primer número', b: 'el segundo número' },
            errors: {
                null: '{field} no puede estar vacío.', blank: '{field} no puede estar vacío.', required: 'Escribe {field}.',
                string: '{field} debe ser una cadena de dígitos.', negative: '{field} debe ser un entero no negativo.',
                digits: '{field} solo puede contener dígitos del 0 al 9.', tooLarge: 'El número supera la longitud permitida.',
                bodyTooLarge: 'La solicitud supera el tamaño permitido.', malformed: 'El formato de la solicitud no es válido.',
                unsupportedMedia: 'Este formato de solicitud no es compatible.', notAcceptable: 'Este formato de respuesta no es compatible.',
                generic: 'Ocurrió un error. Inténtalo de nuevo.'
            },
            step: ({ first, second, carryText, total, digit, carrySentence }) =>
                `Suma ${first} y ${second}${carryText} para obtener ${total}. Escribe ${digit}${carrySentence}`,
            carryText: ({ carry }) => ` más la llevada ${carry}`, carrySentence: ({ carry }) => carry ? `, lleva ${carry}.` : '.',
            finalCarry: ({ carry }) => `Baja la llevada ${carry} al resultado.`
        }
    };

    const languageNames = {
        vi: 'Tiếng Việt', en: 'English', ko: '한국어', ja: '日本語', zh: '中文', es: 'Español'
    };
    const supportedLanguages = Object.keys(translations);
    let currentLanguage = 'vi';

    function getTranslation(key) {
        const valueAt = (language) => key.split('.').reduce((value, part) => value && value[part], translations[language]);
        return valueAt(currentLanguage) ?? valueAt('vi') ?? key;
    }

    function formatTranslation(key, values) {
        const value = getTranslation(key);
        if (typeof value === 'function') return value(values);
        return value.replace(/\{(\w+)\}/g, (_, name) => values[name] ?? '');
    }

    function fieldName(field) {
        return translations[currentLanguage].fields[field] || field;
    }

    function apiErrorMessage(errorBody) {
        const errorMap = {
            INPUT_TOO_LARGE: 'tooLarge',
            MALFORMED_JSON: 'malformed',
            UNSUPPORTED_MEDIA_TYPE: 'unsupportedMedia',
            NOT_ACCEPTABLE: 'notAcceptable'
        };
        if (errorBody.code === 'VALIDATION_ERROR') {
            const message = errorBody.message || '';
            let key = 'digits';
            if (message.includes('null')) key = 'null';
            else if (message.includes('blank')) key = 'blank';
            else if (message.includes('required')) key = 'required';
            else if (message.includes('JSON string')) key = 'string';
            else if (message.includes('non-negative')) key = 'negative';
            return formatTranslation(`errors.${key}`, { field: fieldName(errorBody.field) });
        }
        return formatTranslation(`errors.${errorMap[errorBody.code] || 'generic'}`, {});
    }

    function setLanguage(language) {
        currentLanguage = supportedLanguages.includes(language) ? language : 'vi';
        document.documentElement.lang = currentLanguage;
        document.title = getTranslation('pageTitle');
        document.querySelectorAll('[data-i18n]').forEach(element => {
            element.textContent = getTranslation(element.dataset.i18n);
        });
        document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
            element.placeholder = getTranslation(element.dataset.i18nPlaceholder);
        });
        const languageSelect = document.getElementById('languageSelect');
        if (languageSelect) languageSelect.value = currentLanguage;
        window.dispatchEvent(new CustomEvent('languagechanged', { detail: currentLanguage }));
    }

    window.add2NumI18n = {
        format: formatTranslation,
        errorMessage: apiErrorMessage,
        fieldName,
        languageNames,
        setLanguage,
        getLanguage: () => currentLanguage
    };
})();
