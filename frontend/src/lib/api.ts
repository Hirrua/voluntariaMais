import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {

      console.error("Erro na resposta:", error.response.data);
    } else if (error.request) {

      console.error("Erro na requisição:", error.request);
    } else {

      console.error("Erro:", error.message);
    }
    return Promise.reject(error);
  }
);

export default api;
