source = src/Control.cpp src/Game.cpp src/main.cpp src/Animation.cpp src/Sprite.cpp src/GameObject.cpp \
	src/Platform.cpp src/Character.cpp src/Cat.cpp src/Util.cpp src/Math.cpp src/Camera.cpp src/Cheese.cpp \
	src/Bullet.cpp
header = -I include
libraries_folder = -L lib64
libraries = -lallegro -lallegro_image
object = Control.o Game.o main.o Sprite.o GameObject.o Platform.o Sprite.o Util.o

MiceInvaders_64:
	g++ -g $(source) $(header) $(libraries_folder) $(libraries) -Lstatic_lib64 -llua -ldl -Wl,-rpath lib64 -o MiceInvaders_64

.PHONY : clean

clean:
	rm MiceInvaders_64 *.o
