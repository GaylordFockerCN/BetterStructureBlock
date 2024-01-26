# Forge1.20.1
## 不会再更新此mod，下面会讲实现思路，希望更高版本有人维护。
## 参考了https://github.com/TT432/BetterStructureBlock，感谢该作者的开源分享。
## 关于做这个mod的原因：想生成自定义大结构，但是结构方块限制48*48*48，而创世神官方API导入问题实在不会解决，创世神官方也表示不想管此事甚至不想教学，唯一找到的调用创世神官方API的项目是1.12的https://github.com/Guild-Hall/MHFC，也不知道如何联系作者...于是另辟蹊径想试试自定义没有限制的结构方块，于是找到了上述项目，并且对其迁移至1.20.1
## 实现思路：继承结构方块实体类（StructureBlockEntity），重写detect，load，saveStructure等方法， 把detect方法里面判断范围的数值改一下，然后再继承一下结构方块对应的结构方块类（StructureBlock）、结构方块实体渲染类（StructureBlockEntityRenderer），重写所有带StructureBlock的方法，都得改成BetterStructureBlock不然不会调用重写后的方法。最后记得注册对应的方块、实体和渲染器。detect方法里是取消限制数值的关键，但是就仅仅改了这个地方就得跟着补全很多文件...牵一发而动全身啊...
