#!/bin/bash

# Остановить скрипт при ошибке
set -e

# Определить нужный коммит
TDLIB_COMMIT="a24af099"

# Проверить, установлены ли необходимые утилиты
if ! command -v git &> /dev/null
then
    echo "Git не установлен. Установите его через: brew install git"
    exit
fi

if ! command -v cmake &> /dev/null
then
    echo "CMake не установлен. Установите его через: brew install cmake"
    exit
fi

if ! command -v g++ &> /dev/null
then
    echo "g++ не установлен. Установите его через: brew install gcc"
    exit
fi

# Устанавливаем другие зависимости (если их нет)
brew install openssl zlib

# Клонируем репозиторий TDLib, если он не существует
if [ ! -d "td" ]; then
    git clone https://github.com/tdlib/td.git
else
    echo "TDLib уже клонирован, обновляю репозиторий"
    cd td
    git fetch --all
    cd ..
fi

cd td

# Получаем всю историю репозитория
git fetch --all --tags

# Проверяем, существует ли нужный коммит
if git rev-parse "$TDLIB_COMMIT^{commit}" >/dev/null 2>&1; then
    echo "Переключаемся на коммит $TDLIB_COMMIT"
    git checkout $TDLIB_COMMIT
else
    echo "Коммит $TDLIB_COMMIT не найден. Проверьте правильность коммита."
    exit 1
fi

# Создаем директорию для сборки
rm -rf build
mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DJAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home/ -DOPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl/ -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td -DTD_ENABLE_JNI=ON ..
cmake --build . --target install
cd ..
cd example/java
rm -rf build
mkdir build
cd build
cmake -DCMAKE_BUILD_TYPE=Release -DJAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home/ -DCMAKE_INSTALL_PREFIX:PATH=../../../tdlib -DTd_DIR:PATH=$(greadlink -e ../td/lib/cmake/Td) ..
cmake --build . --target install
cd ../../..
cd ..
ls -l td/tdlib

# Сообщение об успешной сборке
echo "TDLib с поддержкой JNI успешно скомпилирован и установлен"
