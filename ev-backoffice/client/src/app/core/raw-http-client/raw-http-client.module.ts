import { Injectable, NgModule } from '@angular/core';
import { HttpBackend, HttpClient, HttpHandler } from '@angular/common/http';

export function rawHttpClientFactory(handler: HttpHandler) {
  return new HttpClient(handler);
}
@Injectable()
export abstract class RawHttpClient extends HttpClient {
}

@NgModule({
  providers: [
    {
      provide: RawHttpClient,
      useFactory: rawHttpClientFactory,
      deps: [HttpBackend],
    },
  ],
})
export class RawHttpClientModule {
}
