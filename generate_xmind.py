import json
import zipfile
import uuid
import os

def uid():
    return uuid.uuid4().hex[:20]

def make_topic(title, children=None):
    t = {"id": uid(), "class": "topic", "title": title}
    if children:
        t["children"] = {"attached": children}
    return t

root = make_topic("Тестирование пылесоса", [
    make_topic("Основные функции", [
        make_topic("Всасывание", [
            make_topic("Твёрдый пол (паркет, плитка, ламинат)"),
            make_topic("Ковровое покрытие (коротко- и длинноворсовое)"),
            make_topic("Крупный мусор (бумага, крошки)"),
            make_topic("Мелкая пыль"),
            make_topic("Шерсть животных"),
        ]),
        make_topic("Регулировка мощности", [
            make_topic("Минимальная мощность"),
            make_topic("Максимальная мощность"),
            make_topic("Переключение режимов во время работы"),
            make_topic("Плавная регулировка"),
        ]),
        make_topic("Режимы работы", [
            make_topic("Стандартный режим"),
            make_topic("Турбо-режим"),
            make_topic("Тихий / Eco режим"),
            make_topic("Автоматический режим"),
        ]),
        make_topic("Фильтрация", [
            make_topic("Задержка пыли"),
            make_topic("HEPA-фильтр"),
            make_topic("Фильтр после заполнения мешка"),
            make_topic("Фильтрация аллергенов"),
        ]),
    ]),
    make_topic("Конструктив и комплектация", [
        make_topic("Насадки и щётки", [
            make_topic("Основная щётка для пола"),
            make_topic("Насадка для щелей"),
            make_topic("Мягкая насадка для мебели"),
            make_topic("Турбо-щётка для ковров"),
            make_topic("Насадка для матрасов и диванов"),
        ]),
        make_topic("Шланг и трубки", [
            make_topic("Длина шланга"),
            make_topic("Гибкость и изгиб шланга"),
            make_topic("Герметичность соединений"),
            make_topic("Регулировка длины телескопической трубки"),
        ]),
        make_topic("Пылесборник", [
            make_topic("Ёмкость контейнера или мешка"),
            make_topic("Индикатор заполнения"),
            make_topic("Простота замены мешка"),
            make_topic("Герметичность при закрытии"),
        ]),
        make_topic("Кабель и питание", [
            make_topic("Длина сетевого кабеля"),
            make_topic("Автоматическая смотка кабеля"),
            make_topic("Фиксатор кабеля"),
        ]),
    ]),
    make_topic("Безопасность", [
        make_topic("Защита от перегрева", [
            make_topic("Автовыключение при перегреве"),
            make_topic("Индикация перегрева"),
            make_topic("Время охлаждения до повторного включения"),
        ]),
        make_topic("Защита мотора", [
            make_topic("Отключение при засорении шланга"),
            make_topic("Защита при попадании твёрдых предметов"),
            make_topic("Поведение при всасывании воды"),
        ]),
        make_topic("Электробезопасность", [
            make_topic("Изоляция кабеля"),
            make_topic("Заземление"),
            make_topic("Защита вилки от влаги"),
        ]),
        make_topic("Механическая безопасность", [
            make_topic("Устойчивость корпуса при работе"),
            make_topic("Отсутствие острых углов"),
            make_topic("Надёжность фиксации насадок"),
        ]),
    ]),
    make_topic("Производительность", [
        make_topic("Мощность всасывания", [
            make_topic("Тест с бумажным листом"),
            make_topic("Измерение давления воздуха (Па)"),
            make_topic("Сравнение мощности на разных режимах"),
        ]),
        make_topic("Уровень шума", [
            make_topic("Шум на минимальном режиме (дБ)"),
            make_topic("Шум на максимальном режиме (дБ)"),
            make_topic("Соответствие заявленному уровню шума"),
        ]),
        make_topic("Время работы (аккумуляторные)", [
            make_topic("Время работы на максимальной мощности"),
            make_topic("Время работы на минимальной мощности"),
            make_topic("Время полной зарядки"),
            make_topic("Деградация аккумулятора"),
        ]),
        make_topic("Эффективность очистки", [
            make_topic("Процент убранного мусора за один проход"),
            make_topic("Очистка углов и плинтусов"),
            make_topic("Тест после нескольких проходов"),
        ]),
    ]),
    make_topic("Удобство использования", [
        make_topic("Мобильность", [
            make_topic("Вес устройства"),
            make_topic("Манёвренность колёс"),
            make_topic("Высота уборки под мебелью"),
            make_topic("Движение назад"),
        ]),
        make_topic("Управление", [
            make_topic("Расположение кнопок"),
            make_topic("Чёткость нажатия кнопок"),
            make_topic("Понятность индикаторов"),
            make_topic("Управление одной рукой"),
        ]),
        make_topic("Хранение", [
            make_topic("Компактность в сложенном виде"),
            make_topic("Держатель для насадок"),
            make_topic("Устойчивость при вертикальном хранении"),
        ]),
        make_topic("Обслуживание", [
            make_topic("Очистка фильтра"),
            make_topic("Промывка контейнера"),
            make_topic("Чистка щётки от намотанных волос"),
        ]),
    ]),
    make_topic("Надёжность и долговечность", [
        make_topic("Ресурс устройства", [
            make_topic("Ресурс мотора (моточасы)"),
            make_topic("Ресурс фильтра до замены"),
            make_topic("Ресурс аккумулятора"),
        ]),
        make_topic("Стресс-тесты", [
            make_topic("Непрерывная работа 2 часа"),
            make_topic("Многократное включение (50 раз подряд)"),
            make_topic("Работа при крайних температурах"),
        ]),
        make_topic("Механическая прочность", [
            make_topic("Падение с высоты 50 см"),
            make_topic("Прочность кнопок (100+ нажатий)"),
            make_topic("Прочность шланга при изгибе"),
        ]),
        make_topic("Регрессионное тестирование", [
            make_topic("Работа после очистки фильтра"),
            make_topic("Работа с новым мешком vs почти полным"),
            make_topic("Работа после длительного хранения"),
        ]),
    ]),
])

content = [
    {
        "id": uid(),
        "class": "sheet",
        "title": "Sheet 1",
        "rootTopic": root,
        "theme": {"id": uid()}
    }
]

metadata = {"creator": {"name": "XMind", "version": "23.11.3653"}}
manifest = {"file-entries": {"content.json": {}, "metadata.json": {}}}

output_path = r"C:\University\6_semester\KMP\vacuum_test.xmind"

with zipfile.ZipFile(output_path, "w", zipfile.ZIP_DEFLATED) as zf:
    zf.writestr("content.json", json.dumps(content, ensure_ascii=False))
    zf.writestr("metadata.json", json.dumps(metadata, ensure_ascii=False))
    zf.writestr("manifest.json", json.dumps(manifest, ensure_ascii=False))

print(f"Created: {output_path}")
