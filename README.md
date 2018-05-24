# Zeus  

<div align=center><img src="https://github.com/jy01331184/Zeus/blob/master/image/icon.jpg?raw=true">
</div>

### Zeus is a native-based hotfix component with the following features
- without any code injection 
- support dvm&art 4.x-8.0，working in real-time
- java-based without any c/c++ code
- support for all kind of method modifiers & constructors (public/protected/private/static/final etc.)
- support for method called by java-reflect
- support for hotfix rollback
- a gradle plugin is supported for generating a patch by only one step
- high available for all the native method struct is measured dynamically
  
  
  
### how it works

take android 6.0 for example.
first thing is to know the native method struct which present for a java method.
see [art_method.h](http://androidxref.com/6.0.0_r1/xref/art/runtime/art_method.h "Markdown") and
[class.h](http://androidxref.com/6.0.0_r1/xref/art/runtime/mirror/class.h "Markdown")

all the field should be subsitute between a patch method and a target method except *__method_index__*.
(for that the patch method class is in a different classloader with the target method)

and for java reflect invocation, we should write the *__super_class__* field in patch method class to NULL which can avoid 
"*__java.lang.IllegalArgumentException: Expected receiver of type xxx, but got xx__*" Exception

see [reflection-inl.h#VerifyObjectIsClass](http://androidxref.com/6.0.0_r1/xref/art/runtime/reflection-inl.h#103 "Markdown")

