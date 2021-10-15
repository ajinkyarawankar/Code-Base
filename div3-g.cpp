#include <bits/stdc++.h> 
using namespace std; 
int main() 
{ 
    // added the two lines below 
    ios_base::sync_with_stdio(false); 
    cin.tie(NULL); 
    int t,n,m,x;
    cin>>t;
    while(t--){
        cin>>n;
       cin>>m;
       vector<long long> a(n);
       for(int i=0;i<n;i++){
           cin>>a[i];
       }
       for(int i=1;i<n;i++){
           a[i] += a[i-1];
       }
       for(int i=0;i<m;i++){
           cin>>x;
           long long ans = 0;
           bool end = false;
           for(int i=0;i<n;i++){
               if(x==a[i]){
                   ans = i;
                   end = true;
                   break;
               }
           }
           if(!end && a[n-1]<x && a[n-1]>0){
               ans += (x/a[n-1])*n;
               x = x%a[n-1];
               if(x==0){
                   ans--;
                   end = true;
               } 
               if(!end)
               for(int i=0;i<n;i++){
                   if(x==a[i]){
                       ans = ans+i;
                       end = true;
                       break;
                   }
               }
           }
           if(end) cout<<ans<<" ";
           else cout<<"-1"<<" ";
       }
       cout<<"\n";
    }
}