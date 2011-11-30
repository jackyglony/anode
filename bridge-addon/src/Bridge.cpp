#include "Bridge.h"

#include <defines.h>
#include "Env.h"

using namespace v8;

/*
 * Creates an instance of a java module, specified by name.
 */
Handle<Value> Load(const Arguments& args) {
    HandleScope scope;

  /* Check arguments */
  if (args.Length() < 2) {
    return ThrowException(
      Exception::TypeError(String::New("bridge.load(): error: no modulName argument"))
    );
  }

	if(!args[0]->IsString()) {
    return ThrowException(
      Exception::TypeError(String::New("bridge.load(): error: moduleName argument must be a String"))
    );
	}
  
	if(!args[1]->IsObject()) {
    return ThrowException(
      Exception::TypeError(String::New("bridge.load(): error: moduleExports argument must be an Object"))
    );
	}
  
  Local<String> moduleName = args[0]->ToString();
  Local<Object> moduleExports = args[1]->ToObject();
  return scope.Close(Env::getEnv_nocheck()->load(moduleName, moduleExports));
}

void init(Handle<Object> target) {
  	Env::getEnv();
    target->Set(String::NewSymbol("load"),
        FunctionTemplate::New(Load)->GetFunction());
}

NODE_MODULE(bridge, init);
