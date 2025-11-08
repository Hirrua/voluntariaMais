export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  body: string;
  expiresIn: number;
}

export interface UserRegisterRequest {
  nome: string;
  sobrenome: string;
  email: string;
  senha: string;
  cpf: string;
  endereco: {
    logradouro: string;
    bairro: string;
    complemento: string;
    cidade: string;
    estado: string;
    cep: string;
  };
}
