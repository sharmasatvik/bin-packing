import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PackingRequest, PackingResponse} from '../models/packing.model';

@Injectable({providedIn: 'root'})
export class PackingService {
    private apiUrl = 'http://localhost:8080/api/packing';

    constructor(private http: HttpClient) {
    }

    pack(request: PackingRequest): Observable<PackingResponse> {
        return this.http.post<PackingResponse>(`${this.apiUrl}/optimize`, request);
    }
}
