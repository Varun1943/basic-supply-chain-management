import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 200 },
    { duration: '1m', target: 500 },
    // { duration: '2m', target: 800 },
  ],
};

// export const options = {
//   scenarios: {
//     constant_rps: {
//       executor: 'constant-arrival-rate',
//       rate: 5000,
//       timeUnit: '1s',
//       duration: '2m',
//       preAllocatedVUs: 200,
//       maxVUs: 5000,
//     },
//   },
// };
export default function () {

  const payload = JSON.stringify({
    productId: Math.floor(Math.random() * 1000) + 1,
    warehouseId: Math.floor(Math.random() * 5) + 1,
    quantity: 1,
    idempotencyKey: __VU + "-" + __ITER
  });
  // const payload = '{"productId":1,"warehouseId":1,"quantity":1,"idempotencyKey":"' + __VU + "-" + __ITER + '"}';

  const res = http.post('http://localhost:8080/orders', payload, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YXJ1bjEyQGdtYWlsLmNvbSIsImlhdCI6MTc3NzI2NzA5NCwiZXhwIjoxNzc3MzUzNDk0fQ.4tIcR6Dae9roEDCVAtWrhGxH_Rr4HPJhqL1Yy3dn_WA',
    },
  });

  check(res, {
    'status is 200/201': (r) => r.status === 200 || r.status === 201|| r.status === 202,
  });
}

// 
// import http from 'k6/http';
// import { check } from 'k6';

// export const options = {
//   scenarios: {
//     ramp_up: {
//       executor: 'ramping-vus',
//       startVUs: 0,
//       stages: [
//         { duration: '1m', target: 1000 },
//         { duration: '2m', target: 2000 },
//         { duration: '2m', target: 3000 },
//       ],
//     },
//     steady_load: {
//       executor: 'constant-vus',
//       vus: 3000,
//       duration: '3m',
//       startTime: '5m',
//     },
//   },
// };

// export default function () {
//   const payload = JSON.stringify({
//     productId: Math.floor(Math.random() * 1000) + 1,
//     warehouseId: Math.floor(Math.random() * 5) + 1,
//     quantity: 1,
//     idempotencyKey: __VU + "-" + __ITER,
//   });

//   const res = http.post('http://localhost:8080/orders', payload, {
//     headers: {
//       'Content-Type': 'application/json',
//       'Authorization':'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MUBnbWFpbC5jb20iLCJpYXQiOjE3NzYxNTgzMDgsImV4cCI6MTc3NjI0NDcwOH0.S8S6ezTIznEgI0o_rzI2Cp_wQXlkwsWItwKPomWS5QI'
//     },
//   });

//   check(res, {
//     'status ok': (r) => r.status === 200 || r.status === 202,
//   });
// }